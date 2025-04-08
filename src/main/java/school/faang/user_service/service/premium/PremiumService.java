package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PremiumService {

    private final PremiumRepository premiumRepository;
    private final PremiumRetryService premiumRetryService;

    @Value("${user-premium.partition-size}")
    private int partitionSize;

    @Async
    public void removeExpiredPremiumAccess() {
        List<Long> premiumIds = premiumRetryService.getExpiredPremiumIds();
        LocalDateTime now = premiumRetryService.getNow();
        try {
            if (!premiumIds.isEmpty()) {
                List<List<Long>> idBatches = ListUtils.partition(premiumIds, partitionSize);
                for (List<Long> batch : idBatches) {
                    premiumRepository.deleteByIdIn(batch);
                    log.info("Expired premium accesses before date: {} - was deleted in thread - {}",
                            now, Thread.currentThread().getName());
                }
            }
        } catch (Exception e) {
            log.error("Error during expired premium access removal: {}", e.getMessage(), e);
        }
    }

}
