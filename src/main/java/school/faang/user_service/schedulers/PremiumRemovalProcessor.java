package school.faang.user_service.schedulers;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Component
@RequiredArgsConstructor
public class PremiumRemovalProcessor {
    private final PremiumRepository premiumRepository;
    private final Executor taskExecutor;
    @Value("${premium.batch.size}")
    private int batchSize;

    @Transactional
    public void deleteExpiredPremiums() {
        int pageNumber = 0;
        Page<Premium> page;

        do {
            page = premiumRepository.findByEndDateBefore(
                    LocalDateTime.now(),
                    PageRequest.of(pageNumber, batchSize)
            );

            List<Premium> expiredPremiums = page.getContent();
            processBatch(expiredPremiums);

            pageNumber++;
        } while (page.hasNext());
    }

    private void processBatch(List<Premium> batch) {
        List<List<Premium>> chunks = Lists.partition(batch, batchSize);
        chunks.forEach(chunk ->
                CompletableFuture.runAsync(() ->
                                premiumRepository.deleteAll(chunk),
                        taskExecutor
                )
        );
    }
}
