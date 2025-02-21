package school.faang.user_service.service.premium;

import com.google.common.collect.Lists;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.properties.PremiumSchedulerProperties;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class PremiumServiceImpl implements PremiumService {
    private final PremiumRepository premiumRepository;
    private final PremiumSchedulerProperties properties;

    @Transactional
    @Override
    public void removeAllExpiredPremiumAccess() {
        List<Premium> expiredPremium = premiumRepository.findAllByEndDateBefore(LocalDateTime.now());
        List<List<Premium>> batches = new ArrayList<>(Lists.partition(expiredPremium, properties.getBatch()));
        List<CompletableFuture<List<Long>>> futures = batches.stream()
                .map(batch -> removeExpiredPremiumBatchAsync(batch)
                        .handleAsync((ids, ex) -> {
                            if (ex != null) {
                                log.error("Error deleting batch for premium ids: {}", ids, ex);
                            } else {
                                log.info("Success deleting batch for premium ids: {}", ids);
                            }
                            return ids;
                        }))
                .toList();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    @Async(value = "cachedExecutorService")
    public CompletableFuture<List<Long>> removeExpiredPremiumBatchAsync(List<Premium> premiums) {
        premiumRepository.deleteAll(premiums);
        List<Long> premiumsIds = premiums.stream()
                .map(Premium::getId)
                .toList();
        log.info("Deleting expired premiums: {}", premiumsIds);
        return CompletableFuture.completedFuture(premiumsIds);
    }

}
