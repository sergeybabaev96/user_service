package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.premium.PremiumConfig;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class PremiumRemovalJob implements Job {

    private final PremiumRepository premiumRepository;
    private final PremiumConfig premiumConfig;

    @Override
    public void execute(JobExecutionContext context) {
        List<Premium> expiredPremiums = premiumRepository.findAllByEndDateBefore(LocalDateTime.now());

        if (expiredPremiums.isEmpty()) {
            log.info("No expired subscriptions to delete.");
            return;
        }

        log.info("Found {} expired subscriptions. Deleting in batches of {} records…",
                expiredPremiums.size(), premiumConfig.getBatchSize());

        ListUtils.partition(expiredPremiums, premiumConfig.getBatchSize())
                .forEach(this::deleteBatch);
    }

    @Transactional
    private void deleteBatch(List<Premium> batch) {
        premiumRepository.deleteAll(batch);
        log.info("Deleted {} subscriptions. Users: {}", batch.size(),
                batch.stream().map(p -> p.getUser().getId()).toList());
    }
}