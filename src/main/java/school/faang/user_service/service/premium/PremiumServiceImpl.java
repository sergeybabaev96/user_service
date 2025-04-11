package school.faang.user_service.service.premium;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.exchange.ExchangeRequestDto;
import school.faang.user_service.dto.exchange.ExchangeResponseDto;
import school.faang.user_service.dto.payment.CurrencyDto;
import school.faang.user_service.dto.payment.PaymentRequestDto;
import school.faang.user_service.dto.payment.PaymentResponseDto;
import school.faang.user_service.dto.payment.PaymentStatus;
import school.faang.user_service.dto.premium.PremiumAnalyticsDto;
import school.faang.user_service.dto.premium.PremiumNotificationDto;
import school.faang.user_service.dto.premium.PremiumPaymentRequestDto;
import school.faang.user_service.dto.premium.PremiumPaymentResponseDto;
import school.faang.user_service.dto.premium.PremiumRequestDto;
import school.faang.user_service.dto.premium.PremiumResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.enums.premium.PremiumStatus;
import school.faang.user_service.enums.premium.PremiumType;
import school.faang.user_service.exception.AsyncDataNotFoundException;
import school.faang.user_service.exception.PremiumNotActiveException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.kafka.publisher.KafkaPublisher;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;

import static school.faang.user_service.messages.ErrorMessages.NO_ACTIVE_PREMIUM;
import static school.faang.user_service.messages.ErrorMessages.PREMIUM_PAYMENT_RESPONSE_NOT_FOUND;
import static school.faang.user_service.messages.ErrorMessages.PREMIUM_PRICE_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class PremiumServiceImpl implements PremiumService {
    private final UserRepository userRepository;
    private final PremiumRepository premiumRepository;
    private final PremiumMapper premiumMapper;
    private final KafkaPublisher kafkaPublisher;
    private final ConcurrentHashMap<Long, CompletableFuture<PremiumResponseDto>>
            pendingPremiumRequests = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, CompletableFuture<ExchangeResponseDto>>
            pendingPremiumPriceRequests = new ConcurrentHashMap<>();

    @Value("${app.premium.renew-batch-size}")
    private int renewBatchSize;

    @Value("${app.premium.thread-pool-size}")
    private int renewThreadPoolSize;

    @Value("${app.premium.timeout-hours}")
    private int premiumRenewalTimeoutHours;

    @Value("${app.premium.expiry-notification-days}")
    private int premiumExpiryNotificationDays;

    @Value("${spring.kafka.producer.topics.premium.notifications.bought-topic}")
    private String premiumBoughtTopic;

    @Value("${spring.kafka.producer.topics.premium.notifications.expired-topic}")
    private String premiumExpiredTopic;

    @Value("${spring.kafka.producer.topics.premium.notifications.expire-soon-topic}")
    private String premiumExpireSoonTopic;

    @Value("${spring.kafka.producer.topics.premium.notifications.auto-renew-failed-topic}")
    private String premiumAutoRenewFailedTopic;

    @Value("${spring.kafka.producer.topics.premium.notifications.updated-topic}")
    private String premiumUpdatedTopic;

    @Value("${spring.kafka.producer.topics.premium.analytics.premium-analytics-topic}")
    private String premiumAnalyticsTopic;

    @Value("${spring.kafka.producer.topics.premium.notifications.payment-failed-topic}")
    private String premiumPaymentFailedTopic;

    @Value("${spring.kafka.producer.topics.premium.payment.payment-request-topic}")
    private String premiumPaymentRequestTopic;

    @Value("${spring.kafka.producer.topics.premium.payment.price-request-topic}")
    private String premiumPriceRequestTopic;

    @Override
    public CompletableFuture<PremiumResponseDto> buyPremium(PremiumRequestDto premiumRequestDto, boolean byUser) {
        PaymentRequestDto paymentRequestDto = new PaymentRequestDto(premiumRequestDto.getUserId(),
                premiumRequestDto.getPremiumType().getPriceInDollars(), premiumRequestDto.getSelectedCurrency());

        PremiumPaymentRequestDto request = new PremiumPaymentRequestDto(premiumRequestDto, paymentRequestDto,
                CurrencyDto.USD, byUser);

        CompletableFuture<PremiumResponseDto> premiumResponseFuture = new CompletableFuture<>();
        pendingPremiumRequests.put(premiumRequestDto.getUserId(), premiumResponseFuture);
        kafkaPublisher.sendInTransaction(request, premiumPaymentRequestTopic);
        return premiumResponseFuture;
    }

    @Override
    public CompletableFuture<ExchangeResponseDto> getPremiumPrice(PremiumRequestDto premiumRequestDto) {
        ExchangeRequestDto exchangeRequest = ExchangeRequestDto.builder()
                .fromCurrency(CurrencyDto.USD)
                .toCurrency(premiumRequestDto.getSelectedCurrency())
                .amount(premiumRequestDto.getPremiumType().getPriceInDollars())
                .userId(premiumRequestDto.getUserId())
                .build();

        CompletableFuture<ExchangeResponseDto> exchangeResponseFuture = new CompletableFuture<>();
        pendingPremiumPriceRequests.put(premiumRequestDto.getUserId(), exchangeResponseFuture);
        kafkaPublisher.sendInTransaction(exchangeRequest, premiumPriceRequestTopic);
        return exchangeResponseFuture;
    }

    public void fetchPremiumPrice(ExchangeResponseDto exchangeResponse) {
        if (pendingPremiumPriceRequests.containsKey(exchangeResponse.getUserId())) {
            CompletableFuture<ExchangeResponseDto> futurePrice = pendingPremiumPriceRequests.get(exchangeResponse.getUserId());
            futurePrice.complete(exchangeResponse);
        } else {
            String message = PREMIUM_PRICE_NOT_FOUND.formatted(exchangeResponse.getUserId());
            log.error(message);
            throw new AsyncDataNotFoundException(message);
        }
    }

    @Override
    @Transactional
    public void updateAutoRenew(boolean autoRenew, Long userId) {
        User user = getUserById(userId);
        if (user.getPremium() == null) {
            String message = NO_ACTIVE_PREMIUM.formatted(userId);
            log.error(message);
            throw new PremiumNotActiveException(message);
        }
        user.getPremium().setAutoRenew(autoRenew);
    }

    @Override
    public void premiumRenewal() {
        log.info("Premium renew process started");
        ExecutorService executor = Executors.newFixedThreadPool(renewThreadPoolSize);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        int userCount = (int) userRepository.count();

        int batches = (userCount + renewBatchSize - 1) / renewBatchSize;
        IntStream.range(0, batches).forEach(batchNumber -> {
            Pageable pageable = PageRequest.of(batchNumber, renewBatchSize);
            futures.add(CompletableFuture.runAsync(() -> {
                processPremiumRenewal(userRepository.findPremiumActiveUsers(pageable).getContent());
            }, executor));
        });

        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .get(premiumRenewalTimeoutHours, TimeUnit.HOURS);
            log.info("Premium renew process completed");
        } catch (ExecutionException e) {
            log.error("Execution exception while renewing premium", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while renewing premium", e);
            executor.shutdownNow();
            log.warn("Premium renew process was interrupted and may not have completed");
        } catch (TimeoutException e) {
            log.error("Premium renew process didi not complete within {} hours", premiumRenewalTimeoutHours);
        } finally {
            executor.shutdown();
        }
    }

    @Transactional
    private void processPremiumRenewal(List<User> users) {
        for (User user : users) {
            Premium premium = user.getPremium();
            LocalDateTime now = LocalDateTime.now();
            if (!premium.isAutoRenew()) {
                PremiumNotificationDto premiumNotificationDto = PremiumNotificationDto.builder()
                        .userId(user.getId())
                        .build();
                if (premium.getEndDate().isAfter(now)) {
                    log.info("Premium for user with ID {} expired", user.getId());
                    user.setPremium(null);
                    kafkaPublisher.sendInTransaction(premiumNotificationDto, premiumExpiredTopic);
                } else if (premium.getEndDate().plusDays(premiumExpiryNotificationDays).isAfter(now)) {
                    kafkaPublisher.sendInTransaction(premiumNotificationDto, premiumExpireSoonTopic);
                }
            } else {
                int monthDuration = (int) ChronoUnit.MONTHS.between(premium.getStartDate(), premium.getEndDate());
                PremiumRequestDto premiumRequestDto = PremiumRequestDto.builder()
                        .premiumType(PremiumType.getByDuration(monthDuration))
                        .userId(user.getId())
                        .selectedCurrency(premium.getCurrency())
                        .autoRenew(premium.isAutoRenew())
                        .build();

                buyPremium(premiumRequestDto, false);
            }
        }
    }

    public void updatePremium(PremiumPaymentResponseDto premiumPaymentResponse) {
        PaymentResponseDto paymentResponse = premiumPaymentResponse.getPaymentResponseDto();
        PremiumRequestDto premiumRequest = premiumPaymentResponse.getPremiumRequestDto();

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusMonths(premiumRequest.getPremiumType().getMonths());
        PremiumNotificationDto premiumNotification = createPremiumNotificationDto(premiumRequest, startDate, endDate);
        User user = getUserById(premiumRequest.getUserId());

        PremiumAnalyticsDto premiumAnalytics = PremiumAnalyticsDto.builder()
                .amount(premiumRequest.getPremiumType().getPriceInDollars())
                .currency(CurrencyDto.USD.toString())
                .country(user.getCountry().getTitle())
                .startDate(startDate)
                .endDate(endDate)
                .premiumType(premiumRequest.getPremiumType())
                .userId(user.getId())
                .build();

        if (paymentResponse.status() != PaymentStatus.SUCCESS) {
            premiumAnalytics.setPremiumStatus(PremiumStatus.FAILED);
            sendPremiumAnalytics(premiumAnalytics);
            sendPremiumNotification(premiumNotification, premiumRequest.isAutoRenew() ?
                    premiumAutoRenewFailedTopic : premiumPaymentFailedTopic);
            log.error("Payment failed for user with ID: {}", premiumRequest.getUserId());
            return;
        }
        premiumAnalytics.setPremiumStatus(premiumPaymentResponse.isByUser() ?
                PremiumStatus.PURCHASED : PremiumStatus.REFUNDED);

        sendPremiumAnalytics(premiumAnalytics);
        Premium premium = Premium.builder()
                .user(user)
                .startDate(startDate)
                .endDate(endDate)
                .autoRenew(premiumRequest.isAutoRenew())
                .currency(premiumRequest.getSelectedCurrency())
                .build();

        sendPremiumNotification(premiumNotification, premiumPaymentResponse.isByUser() ?
                premiumBoughtTopic : premiumUpdatedTopic);

        premiumRepository.save(premium);
        PremiumResponseDto premiumResponse = premiumMapper.toPremiumResponseDto(premium);
        premiumResponse.setPremiumType(premiumRequest.getPremiumType());

        if (pendingPremiumRequests.containsKey(premiumRequest.getUserId())) {
            CompletableFuture<PremiumResponseDto> premiumFuture = pendingPremiumRequests.remove(premiumRequest.getUserId());
            premiumFuture.complete(premiumResponse);
            log.info("Successfully updated premium for user with ID {}", premiumRequest.getUserId());
        } else {
            String message = PREMIUM_PAYMENT_RESPONSE_NOT_FOUND.formatted(premiumRequest.getUserId());
            log.error(message);
            throw new AsyncDataNotFoundException(message);
        }
    }

    private void sendPremiumNotification(PremiumNotificationDto premiumNotification, String topic) {
        kafkaPublisher.sendInTransaction(premiumNotification, topic);
    }

    private void sendPremiumAnalytics(PremiumAnalyticsDto premiumAnalyticsDto) {
        kafkaPublisher.sendInTransaction(premiumAnalyticsDto, premiumAnalyticsTopic);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("No user with ID " + userId));
    }

    private PremiumNotificationDto createPremiumNotificationDto(PremiumRequestDto premiumRequest,
                                                                LocalDateTime startDate, LocalDateTime endDate) {
        return PremiumNotificationDto.builder()
                .userId(premiumRequest.getUserId())
                .premiumType(premiumRequest.getPremiumType())
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }
}
