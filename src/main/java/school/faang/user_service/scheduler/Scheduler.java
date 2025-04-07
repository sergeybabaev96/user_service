package school.faang.user_service.scheduler;

import org.springframework.scheduling.annotation.Scheduled;

public interface Scheduler {
    @Scheduled(cron = "${events.cleanup.cron}")
    void clearEvents();
}
