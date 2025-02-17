package school.faang.user_service.service.premium.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class PremiumDeletionService {
    private final PremiumRepository premiumRepository;

    @Transactional
    public void deletePremiumsInBatch(List<Premium> batch) {
        try {
            premiumRepository.deleteAllByIdsInBatch(batch.stream().map(Premium::getId).toList());
            log.info("Deleted {} premium accounts", batch.size());
        } catch (Exception e) {
            log.error("Error occurred while deleting the batch: {}", e.getMessage(), e);
        }
    }
}