package school.faang.user_service.schedulers;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.PremiumService;

@Component
@RequiredArgsConstructor
public class PremiumRemover {
    private final PremiumService premiumService;

    @Scheduled(cron = "${premium.schedule.removal-cron}")
    public void removePremium() {
        premiumService.deleteExpiredPremiums();
    }
}
