package school.faang.user_service.service.event;

import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventRequestDto;
import school.faang.user_service.entity.event.Event;

import java.util.List;

public interface EventService {
    List<EventDto> getEventsByFilter(EventFilterDto filter);

    EventDto getEvent(Long id);

    EventDto createEvent(EventRequestDto eventData);

    EventDto updateEvent(EventRequestDto eventData, Long id);

    void deleteEvent(Long id);

    List<EventDto> getOwnedEvents(Long userId);

    List<EventDto> getParticipatedEvents(Long userId);

    Event findByIdOrThrow(long eventId);

    void banUsers(List<Long> userIdsToBan);
}
