package school.faang.user_service.scheduler;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.scheduler.EventStartEventNotificationConfig;
import school.faang.user_service.dto.event.EventStartDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.publisher.event.NotificationEventStartEventPublisher;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class EventScheduler {
    private final EventService eventService;
    private final NotificationEventStartEventPublisher eventStartEventPublisher;
    private final EventStartEventNotificationConfig eventNotificationConfig;
    private final Environment environment;

    @PostConstruct
    private void init() {
        log.info("Event scheduler initialized with frequency: {}", environment.getProperty("event.removal.cron"));
    }

    @Scheduled(cron = "${event.removal.cron}")
    public void clearEvents() {
        int countEvents = eventService.clearEvents();
        log.info("Deleted {} events", countEvents);
    }

    @Scheduled(cron = "${event.start-notification.cron}")
    public void scheduleEventNotifications() {
        LocalDateTime now = LocalDateTime.now();
        eventNotificationConfig.getIntervals().forEach( interval -> {
            LocalDateTime startTime = now.plusMinutes(interval.getTime());
            LocalDateTime doubleStartTime = now.plusMinutes(interval.getTime() * 2L);
            scheduleNotificationsForTimeFrame(startTime, doubleStartTime, interval.getMessage());
        });
    }

    private void scheduleNotificationsForTimeFrame(LocalDateTime startTime, LocalDateTime doubleStartTime, String message) {
        List<Event> events = eventService.getEventsStartingAt(startTime, doubleStartTime);

        events.forEach(event -> {
            EventStartDto eventStartDto = EventStartDto.builder()
                    .eventId(event.getId())
                    .ownerId(event.getOwner().getId())
                    .userIds(event.getAttendees().stream().map(User::getId).toList())
                    .startTime(event.getStartDate())
                    .message(message)
                    .build();
            eventStartEventPublisher.publishEvent(eventStartDto);
            log.info("Scheduled notification for event {} with message: {}", event.getId(), message);
        });
    }
}
