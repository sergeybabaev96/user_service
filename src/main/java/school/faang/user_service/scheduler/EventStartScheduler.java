package school.faang.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.messaging.event.EventStartEvent;
import school.faang.user_service.messaging.publisher.EventStartEventPublisher;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventStartScheduler {

    private final EventRepository eventRepository;
    private final UserService userService;
    private final EventStartEventPublisher eventStartEventPublisher;

    @Scheduled(fixedRate = 60000)
    public void publishStartEvents() {
        List<Event> startingNow = eventRepository.findAll().stream()
                .filter(event -> event.getStartDate() != null &&
                        event.getStartDate().isBefore(LocalDateTime.now().plusMinutes(1)) &&
                        event.getStartDate().isAfter(LocalDateTime.now().minusMinutes(1)))
                .toList();

        for (Event event : startingNow) {
            List<Long> participantIds = event.getAttendees().stream()
                    .map(user -> user.getId())
                    .toList();

            List<UserDto> participants = userService.getUsersByIds(participantIds);

            EventStartEvent eventStartEvent = new EventStartEvent(
                    String.valueOf(event.getId()),
                    participants
            );

            eventStartEventPublisher.publish(eventStartEvent);
            log.info("Published EventStartEvent for event ID {}", event.getId());
        }
    }
}
