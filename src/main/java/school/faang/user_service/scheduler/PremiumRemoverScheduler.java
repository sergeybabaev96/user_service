package school.faang.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.premium.PremiumService;

@Slf4j
@Component
@RequiredArgsConstructor
public class PremiumRemoverScheduler {
    private final PremiumService premiumService;

    @Scheduled(cron = "${premium.scheduler.remove-cron}")
    @SchedulerLock(name = "removePremium")
    public void removePremium() {
        log.info("Deleting expired premium...");
        premiumService.removeAllExpiredPremiumAccess();
        log.info("Expired premium deleted");
    }
}