package school.faang.user_service.service.premium;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Getter
public class PremiumRetryService {

    private final PremiumRepository premiumRepository;
    private final LocalDateTime now = LocalDateTime.now();

    @Retryable(
            value = RuntimeException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    @Transactional(readOnly = true)
    public List<Long> getExpiredPremiumIds() {
        List<Long> expiredPremiumIds = premiumRepository.findIdsByEndDateBefore(now);
        log.info("Got expired premium ids from DB, time {}", now);
        return expiredPremiumIds;
    }
}
