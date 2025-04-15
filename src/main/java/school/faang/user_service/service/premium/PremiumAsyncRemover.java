package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.util.List;

/**
 * Service for asynchronously removing expired premiums.
 * This service uses a transaction template to ensure that the removal of expired premiums
 * is done in a transactional context.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PremiumAsyncRemover {
    private final PremiumRepository premiumRepository;
    private final TransactionTemplate transactionTemplate;

    /**
     * Asynchronously removes expired premiums from the database.
     * This method is annotated with @Async to run in a separate thread.
     * It also uses @Retryable to retry the operation in case of a RuntimeException.
     *
     * @param expiredPremiums List of expired premiums to be removed.
     */
    @Async("premiumRemoverExecutor")
    @Retryable(
            value = {RuntimeException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void removeBatchAsync(List<Premium> expiredPremiums) {
            transactionTemplate.executeWithoutResult(status -> {
                premiumRepository.deleteAll(expiredPremiums);
                log.info("Successfully deleted {} expired premiums", expiredPremiums.size());
            });
    }

    /**
     * Recovery method for handling failures after retries.
     * This method is called when the maximum number of retry attempts is reached.
     *
     * @param ex the exception that caused the retries to fail
     * @param batch the batch of expired premiums that failed to be removed
     */
    @Recover
    public void recover(RuntimeException ex, List<Premium> batch) {
        log.error("Failed to delete batch after retries: {}", batch.size(), ex);
    }
}
