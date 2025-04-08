package school.faang.user_service.service.premium;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Getter
public class PremiumRetryService {

    private final PremiumRepository premiumRepository;
    private LocalDateTime now;
    private List<Long> premiumIds = new ArrayList<>();

    @Retryable(
            value = RuntimeException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public List<Long> getExpiredPremiumIds() {
        now = LocalDateTime.now();
        List<Premium> expiredPremiums = premiumRepository.findAllByEndDateBefore(now);
        log.info("Got expired premium ids from DM, time {}", now);
        premiumIds = expiredPremiums.stream()
                .map(Premium::getId)
                .toList();
        return premiumIds;
    }
}
