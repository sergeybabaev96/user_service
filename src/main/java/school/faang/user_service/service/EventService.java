package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    public void deleteEventByUserId(Long userId) {
        List<Event> eventToUser = eventRepository.findAllByUserId(userId);
        eventRepository.deleteAll(eventToUser);
    }

    public void deleteParticipationFromEvent(Long userId) {
        List<Event> eventsWhereUserParticipation = eventRepository.findParticipatedEventsByUserId(userId);
        for (Event event : eventsWhereUserParticipation) {
            List<User> participationWithoutDeactivatedUser = event.getAttendees().stream()
                    .filter(user-> !Objects.equals(user.getId(), userId)).toList();
            event.setAttendees(participationWithoutDeactivatedUser);
        }
        eventRepository.saveAll(eventsWhereUserParticipation);
    }
}
