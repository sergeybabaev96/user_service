package school.faang.user_service.scheduler;

import java.util.List;
import lombok.RequiredArgsConstructor;
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
        List<Premium> premiums = premiumService.defineExpirePremium();
        int size = premiums.size();
        int numberOfBatches = (size + batchSize - 1) / batchSize;

        for (int i = 0; i < numberOfBatches; i++) {
            int startIndex = i * batchSize;
            int endIndex = Math.min(startIndex + batchSize, size);

            List<Premium> batches = premiums.subList(startIndex, endIndex);
            premiumService.removePremium(batches);
        }
    }

}
