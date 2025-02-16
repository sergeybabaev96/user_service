package school.faang.user_service.schedulers;

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

@Component
@RequiredArgsConstructor
public class PremiumExpirationManager {
    private final PremiumRepository premiumRepository;
    private final AsyncPremiumRemovalService asyncPremiumRemovalService;

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
            asyncPremiumRemovalService.processBatch(expiredPremiums, batchSize);
            pageNumber++;
        } while (page.hasNext());
    }
}
