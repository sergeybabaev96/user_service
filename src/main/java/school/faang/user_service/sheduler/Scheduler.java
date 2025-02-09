package school.faang.user_service.sheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import school.faang.user_service.service.EventService;

@Service
@RequiredArgsConstructor
public class Scheduler {

    private final EventService eventService;

    @Async
    @Scheduled(cron = "${events.delete.cron}")
    public void clearEvents(){
        eventService.clearEvents();
    }
}
