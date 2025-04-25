package school.faang.user_service.service.premium;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PremiumService {

    private final PremiumBatchService premiumBatchService;
    private final PremiumRetryService premiumRetryService;

    @Value("${user-premium.partition-size}")
    @Min(1)
    private int partitionSize;

    @Async
    public void removeExpiredPremiumAccess() {
        List<Long> premiumIds = premiumRetryService.getExpiredPremiumIds();
        try {
            if (!premiumIds.isEmpty()) {
                List<List<Long>> batches = ListUtils.partition(premiumIds, partitionSize);
                for (List<Long> batch : batches) {
                    premiumBatchService.removeBatch(batch, premiumRetryService.getNow());
                }
            }
        } catch (Exception e) {
            log.error("Error during expired premium access removal: {}", e.getMessage(), e);
        }
    }
}
