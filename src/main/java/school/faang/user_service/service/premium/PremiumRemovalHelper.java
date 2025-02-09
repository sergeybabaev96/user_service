package school.faang.user_service.service.premium;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class PremiumRemovalHelper {

    private final PremiumRepository premiumRepository;

    @Transactional
    public void deleteBatch(List<Premium> batch) {
        premiumRepository.deleteAll(batch);
        log.info("Deleted {} subscriptions. Users: {}", batch.size(),
                batch.stream().map(p -> p.getUser().getId()).toList());
    }
}