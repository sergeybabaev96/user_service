package school.faang.user_service.job;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import school.faang.user_service.service.premium.PremiumService;

@Service
@RequiredArgsConstructor
public class PremiumRemover {
    private final PremiumService premiumService;

    @Scheduled(cron = "${job.removePremiumCron}")
    public void removePremium() {
        premiumService.deleteExpiredPremiumsAsync();
    }
}
