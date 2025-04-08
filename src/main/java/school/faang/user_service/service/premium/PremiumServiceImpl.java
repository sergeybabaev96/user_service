package school.faang.user_service.service.premium;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.exchange.ExchangeRequestDto;
import school.faang.user_service.dto.exchange.ExchangeResponseDto;
import school.faang.user_service.dto.payment.CurrencyDto;
import school.faang.user_service.dto.payment.PaymentRequestDto;
import school.faang.user_service.dto.payment.PaymentResponseDto;
import school.faang.user_service.dto.payment.PaymentStatus;
import school.faang.user_service.dto.premium.PremiumRequestDto;
import school.faang.user_service.dto.premium.PremiumResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.enums.premium.PremiumType;
import school.faang.user_service.exception.PremiumNotActiveException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;

import static school.faang.user_service.messages.ErrorMessages.NO_ACTIVE_PREMIUM;

@Slf4j
@Service
@RequiredArgsConstructor
public class PremiumServiceImpl implements PremiumService {
    private final PaymentServiceClient paymentServiceClient;
    private final UserRepository userRepository;
    private final PremiumRepository premiumRepository;
    private final PremiumMapper premiumMapper;
    private final KafkaTemplate kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.premium.renew-batch-size}")
    private int renewBatchSize;

    @Value("${app.premium.thread-pool-size}")
    private int renewThreadPoolSize;

    @Value("${app.premium.timeout-hours}")
    private int premiumRenewalTimeoutHours;

    @Value("${app.premium.expiry-notification-days}")
    private int premiumExpiryNotificationDays;

    @Value("${spring.kafka.topics.premium.notification.bought-topic}")
    private String premiumBoughtTopic;

    @Value("${spring.kafka.topics.premium.notification.expired-topic}")
    private String premiumExpiredTopic;

    @Value("${spring.kafka.topics.premium.notification.expire-soon-topic}")
    private String premiumExpireSoonTopic;

    @Value("${spring.kafka.topics.premium.notification.auto-renew-failed-topic}")
    private String premiumAutoRenewFailedTopic;

    @Value("${spring.kafka.topics.premium.notification.updated-topic}")
    private String premiumUpdatedTopic;

    @Value("${spring.kafka.topics.premium.notification.updated-topic}")
    private String premiumAnalyticsTopic;

    @Override
    @Transactional
    public ResponseEntity<PremiumResponseDto> buyPremium(PremiumRequestDto premiumRequestDto) {
        BigDecimal amount = getPremiumPrice(premiumRequestDto).getAmount();
        log.info("Calculated amount for premium type {} is {} {}",
                premiumRequestDto.getPremiumType(), amount, premiumRequestDto.getCurrency());
        PaymentRequestDto paymentRequestDto = new PaymentRequestDto(premiumRequestDto.getUserId(),
                amount, premiumRequestDto.getCurrency());

        ResponseEntity<PaymentResponseDto> response = paymentServiceClient.sendPayment(paymentRequestDto);
        if (response.getStatusCode().isError() || Objects.requireNonNull(
                response.getBody()).status() != PaymentStatus.SUCCESS) {
            log.error("Payment failed with status: {}", response.getStatusCode());
            return ResponseEntity.status(response.getStatusCode())
                    .body(PremiumResponseDto.builder().userId(premiumRequestDto.getUserId()).build());
        }
        User user = getUserById(premiumRequestDto.getUserId());

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusMonths(premiumRequestDto.getPremiumType().getMonths());
        Premium premium = Premium.builder()
                .user(user)
                .startDate(startDate)
                .endDate(endDate)
                .autoRenew(premiumRequestDto.isAutoRenew())
                .currency(premiumRequestDto.getCurrency())
                .build();

        //premium-bought-topic
        premiumRepository.save(premium);
        PremiumResponseDto premiumResponse = premiumMapper.toPremiumResponseDto(premium);
        premiumResponse.setPremiumType(premiumRequestDto.getPremiumType());
        return ResponseEntity.ok(premiumResponse);
    }

    @Override
    public ExchangeResponseDto getPremiumPrice(PremiumRequestDto premiumRequestDto) {
        ExchangeRequestDto exchangeRequest = new ExchangeRequestDto(CurrencyDto.USD,
                premiumRequestDto.getCurrency(), premiumRequestDto.getPremiumType().getPriceInDollars());
        return null;/////////сделать потом
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
                if (premium.getEndDate().isAfter(now)) {
                    log.info("Premium for user with ID {} expired", user.getId());
                    user.setPremium(null);
                    //premium-expired-topic
                } else if (premium.getEndDate().plusDays(premiumExpiryNotificationDays).isAfter(now)) {
                    //premium-expire-soon-topic
                }
            } else {
                int monthDuration = (int) ChronoUnit.MONTHS.between(premium.getStartDate(), premium.getEndDate());
                PremiumRequestDto premiumRequestDto = PremiumRequestDto.builder()
                        .premiumType(PremiumType.getByDuration(monthDuration))
                        .userId(user.getId())
                        .currency(premium.getCurrency())
                        .autoRenew(premium.isAutoRenew())
                        .build();

                BigDecimal amount = getPremiumPrice(premiumRequestDto).getAmount();
                PaymentRequestDto paymentRequestDto = new PaymentRequestDto(premiumRequestDto.getUserId(),
                        amount, premiumRequestDto.getCurrency());
                ResponseEntity<PaymentResponseDto> response = paymentServiceClient.sendPayment(paymentRequestDto);

                if (response.getStatusCode().isError() || Objects.requireNonNull(
                        response.getBody()).status() != PaymentStatus.SUCCESS) {
                    log.info("Payment failed for premium for user with ID {}", user.getId());
                    //premium-auto-renew-failed-topic
                } else {
                    LocalDateTime endDate = now.plusMonths(premiumRequestDto.getPremiumType().getMonths());
                    premium.setStartDate(now);
                    premium.setEndDate(endDate);
                    log.info("Premium for user with ID {} updated and lasts from {} to {}",
                            user.getId(), now, endDate);
                    //premium-updated-topic
                }
            }
        }
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("No user with ID " + userId));
    }
}
