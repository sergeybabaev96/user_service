package school.faang.user_service.scheduler;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.service.premium.PremiumService;

@Component
@RequiredArgsConstructor
public class PremiumRemover {

    @Value(value = "${data.batch-size}")
    private int batchSize;

    private final PremiumService premiumService;

    @Scheduled(cron = "${scheduler.cron.premium-remove}")
    public void removePremium() {
        List<Premium> readyToPublishPosts = premiumService.defineExpirePremium();
        List<List<Premium>> batches = ListUtils.partition(readyToPublishPosts, batchSize);
        batches.forEach(premiumService::removePremium);
    }
}
