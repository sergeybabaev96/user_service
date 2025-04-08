package school.faang.user_service.service;

import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface EventService {
    EventDto create(EventDto eventDto);

    EventDto getEvent(Long eventId);

    List<EventDto> getEventsByFilter(EventFilterDto eventFilterDto);

    void deleteEvent(Long eventId);

    EventDto updateEvent(EventDto eventDto);

    List<EventDto> getParticipatedEvents(Long userId);

    void deleteEventByUserId(Long userId);

    void deleteParticipationFromEvent(Long userId);

    List<CompletableFuture<Void>> cleanPastEvents();
}
