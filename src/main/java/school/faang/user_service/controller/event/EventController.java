package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventUpdateDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.service.EventService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class EventController {


    private final EventService eventService;


    public EventDto create(EventDto event) {
        return eventService.create(event);
    }

    public EventDto update(EventUpdateDto eventDto) {
        return eventService.updateEvent(eventDto);
    }

    public EventDto getEvent(Long eventId) {
        return eventService.getEvent(eventId);
    }

    public void deleteEvent(EventDto event) {
        eventService.deleteEvent(event.getId());
    }

    public List<Event> getParticipatedEvents(long userId) {
        return eventService.getParticipatedEvents(userId);
    }

    public List<EventDto> getOwnedEvents(Long ownerId) {
        return eventService.getOwnedEvents(ownerId);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        return eventService.getEventsByFilter(filter);
    }
}
