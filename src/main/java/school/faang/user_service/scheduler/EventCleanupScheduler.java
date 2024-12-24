package school.faang.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.EventCleanupException;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventCleanupScheduler {

    private final EventService eventService;

    @Retryable(retryFor = Exception.class, backoff = @Backoff(delay = 5000, multiplier = 2))
    @Scheduled(cron = "${cron.expressions.clear-events}")
    public void clearEvents() {
        try {
            log.info("Scheduled task: Clear 'completed' and 'canceled' events started at {}", LocalDateTime.now());
            eventService.deleteCompletedAndCanceledEvents();
            log.info("Scheduled task: Clear 'completed' and 'canceled' events finished at {}", LocalDateTime.now());
        } catch (Exception e) {
            log.error("Failed to clear 'completed' and 'canceled' events: {}", e.getMessage(), e);
            throw new EventCleanupException("Failed to clear 'completed' and 'canceled' events");
        }
    }
}
