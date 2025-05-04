package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PremiumBatchService {

    private final PremiumRepository premiumRepository;

    @Transactional()
    public void removeBatch(List<Long> batch, LocalDateTime now) {
        premiumRepository.deleteByIdIn(batch);
        log.info("Expired premium accesses before date: {} - was deleted in thread - {}",
                now, Thread.currentThread().getName());
    }
}
