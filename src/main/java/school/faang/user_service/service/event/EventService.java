package school.faang.user_service.service.event;

import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.filter.event.EventFilterDto;

import java.util.List;

public interface EventService {
    EventDto create(EventDto event);

    EventDto getEvent(long id);

    List<EventDto> getEventByFilters(EventFilterDto filter);

    void deleteEvent(long id);

    EventDto updateEvent(EventDto eventDto);

    List<EventDto> getOwnedEvents(long userId);

    List<EventDto> getParticipatedEvents(long userId);
}
