package school.faang.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.EventService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Override
    public void deactivateEventsByUserId(long userId) {
        List<Long> eventListId = eventRepository.findAllByUserId(userId)
                .stream()
                .filter(event -> event.getStatus() == EventStatus.PLANNED)
                .map(Event::getId)
                .toList();
        if (!eventListId.isEmpty()) {
            eventRepository.deleteUserParticipationByEventId(eventListId);
            eventRepository.deleteAllById(eventListId);
        }
    }
}
