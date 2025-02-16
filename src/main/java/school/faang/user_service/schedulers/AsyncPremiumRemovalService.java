package school.faang.user_service.schedulers;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AsyncPremiumRemovalService {

    private final PremiumRepository premiumRepository;

    @Async
    public void processBatch(List<Premium> batch, int batchSize) {
        List<List<Premium>> chunks = Lists.partition(batch, batchSize);
        chunks.forEach(premiumRepository::deleteAll);
    }
}
