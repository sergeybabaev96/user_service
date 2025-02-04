package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.config.PremiumConfig;
import school.faang.user_service.dto.payment.Currency;
import school.faang.user_service.dto.payment.PaymentRequest;
import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.exception.PaymentFailedException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RequiredArgsConstructor
@Service
public class PremiumService {
    private final PremiumRepository premiumRepository;
    private final UserRepository userRepository;
    private final PaymentServiceClient paymentServiceClient;
    private final PremiumConfig premiumConfig;
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    @Transactional
    public Premium buyPremium(long userId, PremiumPeriod premiumPeriod) {

        User user = userRepository.findById(userId).orElseThrow();
        if (premiumRepository.existsByUserId(user.getId())) {
            throw new IllegalStateException("The user with id " + userId + " already has a premium subscription.");
        }

        makePayment(premiumPeriod);

        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime premiumEndDate = currentDateTime.plusDays(premiumPeriod.getDays());

        Premium premium = Premium.builder()
                .user(user)
                .startDate(currentDateTime)
                .endDate(premiumEndDate)
                .build();

        return premiumRepository.save(premium);
    }

    @Transactional(readOnly = true)
    public List<Long> getPremiumUsers() {
        return premiumRepository.findAllPremiumUsers();
    }

    public void removeExpiredPremiums() {
        LocalDateTime now = LocalDateTime.now();
        List<Premium> expiredPremiums = premiumRepository.findAllByEndDateBefore(now);

        if (expiredPremiums.isEmpty()) {
            log.info("No expired subscriptions to delete");
            return;
        }

        int batchSize = premiumConfig.getBatchSize();
        log.info("Found {} expired subscriptions. Deleting in batches of {} records…", expiredPremiums.size(), batchSize);

        for (int i = 0; i < expiredPremiums.size(); i += batchSize) {
            int end = Math.min(i + batchSize, expiredPremiums.size());
            List<Premium> batch = expiredPremiums.subList(i, end);

            executorService.submit(() -> deleteBatch(batch));
        }
    }

    private void makePayment(PremiumPeriod premiumPeriod) {
        long paymentNumber = UUID.randomUUID().getLeastSignificantBits();
        BigDecimal amount = BigDecimal.valueOf(premiumPeriod.getPrice());
        PaymentRequest paymentRequest = new PaymentRequest(paymentNumber, amount, Currency.USD);

        ResponseEntity<PaymentResponse> paymentResponse = paymentServiceClient.sendPayment(paymentRequest);

        if (paymentResponse.getStatusCode() != HttpStatus.OK) {
            String message = Objects.requireNonNull(paymentResponse.getBody()).message();
            throw new PaymentFailedException(message);
        }
    }

    @Transactional
    private void deleteBatch(List<Premium> batch) {
        premiumRepository.deleteAll(batch);
        log.info("{} subscriptions deleted", batch.size());
    }
}