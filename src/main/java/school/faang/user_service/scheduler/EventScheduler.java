package school.faang.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.event.EventService;

@Slf4j
@RequiredArgsConstructor
@Component
public class EventScheduler {

    private final EventService eventService;

    @Scheduled(cron = "${event.removal.cron}")
    public void clearEvents() {
        int countEvents = eventService.clearEvents();
        log.info("Deleted {} events", countEvents);
    }
}
