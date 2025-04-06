package school.faang.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.event.EventService;

@Slf4j
@Component
@RequiredArgsConstructor
public class Scheduler {
    private final EventService eventService;

    @Value("${scheduler.event-cleanup.batch-size}")
    private int batchSize;

    @Scheduled(cron = "${scheduler.event-cleanup.cron}")
    public void clearEvents() {
        log.info("Старт задачи очистки завершённых событий");
        eventService.deletePastEvents(batchSize);
    }
}
