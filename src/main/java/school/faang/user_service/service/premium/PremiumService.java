package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for managing premium subscriptions.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PremiumService {
    private final PremiumRepository premiumRepository;
    private final PremiumAsyncRemover premiumAsyncRemover;

    /**
     * Removes expired premium subscriptions in batches.
     *
     * @param batchSize the size of each batch to process
     */
    public void removeExpiredPremium(int batchSize) {
        List<Premium> expiredPremiums = premiumRepository.findAllByEndDateBefore(LocalDateTime.now());
        if (expiredPremiums.isEmpty()) {
            log.info("No expired premiums found");
            return;
        }
        log.info("Found {} expired premiums", expiredPremiums.size());
        ListUtils.partition(expiredPremiums, batchSize)
                .forEach(premiumAsyncRemover::removeBatchAsync);
    }
}
