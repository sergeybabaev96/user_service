package school.faang.user_service.service.premium.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.PaymentServiceFeignClient;
import school.faang.user_service.common.PaymentStatus;
import school.faang.user_service.common.PremiumPeriod;
import school.faang.user_service.config.JobProperties;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.PremiumDto;
import school.faang.user_service.dto.payment.PaymentRequest;
import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exception.PremiumInvalidDataException;
import school.faang.user_service.exception.PremiumNotFoundException;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.premium.PremiumService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Log4j2
@Service
@RequiredArgsConstructor
public class PremiumServiceImpl implements PremiumService {
    private final PremiumRepository premiumRepository;
    private final PremiumMapper premiumMapper;
    private final UserRepository userRepository;
    private final PaymentServiceFeignClient paymentServiceClient;
    private final UserContext userContext;
    private final JobProperties jobProperties;

    @Override
    public PremiumDto buyPremium(Integer days) {
        PremiumPeriod premiumPeriod = PremiumPeriod.fromDays(days);

        User user = validateAndGetUser(userContext.getUserId());
        PaymentRequest paymentRequest = createPaymentRequest(premiumPeriod);
        PaymentResponse paymentResponse = sendPaymentRequest(paymentRequest);

        if (paymentResponse.status().equals(PaymentStatus.SUCCESS)) {
            Premium premium = savePremium(premiumPeriod, user);
            return premiumMapper.toDto(premium);
        }
        throw new PremiumInvalidDataException(String.format("Error from paymentService: %s", paymentResponse.message()));
    }

    @Override
    public void deleteExpiredPremiumsAsync() {
        List<Premium> allByEndDateBefore = premiumRepository.findAllByEndDateBefore(LocalDateTime.now());

        if (allByEndDateBefore.isEmpty()) {
            log.info("No expired premium for deletion.");
            return;
        }

        List<List<Premium>> premiumBatches = getPremiumBatches(allByEndDateBefore, jobProperties.getBatchSize());
        ExecutorService executorService = Executors.newFixedThreadPool(jobProperties.getThreadPoolSize());
        List<Future<?>> futures = new ArrayList<>();

        for (List<Premium> batch : premiumBatches) {
            futures.add(executorService.submit(() -> deletePremiumsInBatch(batch)));
        }
        waitForCompletion(futures);
        executorService.shutdown();
    }

    private void deletePremiumsInBatch(List<Premium> batch) {
        try {
            premiumRepository.deleteAllByIdsInBatch(batch.stream().map(Premium::getId).toList());
        } catch (Exception e) {
            log.info("Deleted {} premium accounts", batch.size());
            log.error("Error occurred while deleting the batch: {}", e.getMessage(), e);
        }
    }

    private List<List<Premium>> getPremiumBatches(List<Premium> allExpired, Integer batchSize) {
        List<List<Premium>> batches = new ArrayList<>();
        for (int i = 0; i < allExpired.size(); i += batchSize) {
            batches.add(allExpired.subList(i, Math.min(allExpired.size(), i + batchSize)));
        }
        return batches;
    }

    protected void waitForCompletion(List<Future<?>> futures) {
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("Error executing thread: {}", e.getMessage(), e);
                Thread.currentThread().interrupt();
            }
        }
    }

    private User validateAndGetUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new PremiumNotFoundException(String.format("No user found by this userId: %s", userId)));
        if (premiumRepository.existsByUserId(user.getId())) {
            throw new PremiumInvalidDataException(String.format("User with id: %s already has a Premium", userId));
        }
        return user;
    }

    private PaymentRequest createPaymentRequest(PremiumPeriod premiumPeriod) {
        return PaymentRequest.builder()
                .paymentNumber(Instant.now().toEpochMilli())
                .amount(premiumPeriod.getPrice())
                .currency(premiumPeriod.getCurrency())
                .build();
    }

    private PaymentResponse sendPaymentRequest(PaymentRequest paymentRequest) {
        return paymentServiceClient.sendPayment(paymentRequest).getBody();
    }

    private Premium savePremium(PremiumPeriod premiumPeriod, User user) {
        LocalDateTime now = LocalDateTime.now();
        return premiumRepository.save(Premium.builder()
                .user(user)
                .startDate(now)
                .endDate(now.plusDays(premiumPeriod.getDays()))
                .build());
    }
}
