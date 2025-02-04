package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class PremiumRemover {
    private final PremiumService premiumService;

    @Scheduled(cron = "${premium.removal.cron}")
    public void schedulePremiumRemoval() {
        log.info("Scheduled task started: Removing expired premium subscriptions");
        premiumService.removeExpiredPremiums();
    }
}
