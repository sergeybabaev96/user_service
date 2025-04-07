package school.faang.user_service.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Scheduler {

    @Scheduled(cron = "${events.cleanup.cron}")
    public void clearEvents() {

    }
}
