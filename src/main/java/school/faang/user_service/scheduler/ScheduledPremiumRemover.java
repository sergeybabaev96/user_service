package school.faang.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.premium.PremiumService;

/**
 * Scheduled task to remove expired premium subscriptions.
 * This class is responsible for scheduling the removal of expired premium subscriptions
 * at a fixed rate defined in the application properties.
 */
@Component
@RequiredArgsConstructor
public class ScheduledPremiumRemover {
    private final PremiumService premiumService;

    @Value("${premium-remover.batch-size}")
    private int batchSize;

    /**
     * Scheduled method to remove expired premium subscriptions.
     * This method is executed at a fixed rate defined in the application properties.
     */
    @Scheduled(cron = "${premium-remover.cron}")
    public void removeExpiredPremium() {
        premiumService.removeExpiredPremium(batchSize);
    }
}
