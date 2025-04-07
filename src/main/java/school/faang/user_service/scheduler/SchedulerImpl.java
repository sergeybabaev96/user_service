package school.faang.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.EventService;

@Component
@RequiredArgsConstructor
public class SchedulerImpl implements Scheduler {
    private final EventService eventService;

    @Override
    @Scheduled(cron = "${events.cleanup.cron}")
    public void clearEvents() {
        eventService.cleanPastEvents();
    }
}
