package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.filter.EventFilterDto;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.validator.EventDtoValidator;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    public EventDto create(EventDto event) {
        EventDtoValidator.validate(event);
        return eventService.create(event);
    }

    public EventDto getEvent(long eventId) {
        return eventService.getEvent(eventId);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        return eventService.getEventsByFilter(filter);
    }

    public void deleteEvent(long eventId) {
        eventService.deleteEvent(eventId);
    }

    public EventDto updateEvent(EventDto event) {
        EventDtoValidator.validate(event);
        return eventService.updateEvent(event);
    }

    public List<EventDto> getOwnedEvents(long userId) {
        return eventService.getOwnedEvents(userId);
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        return eventService.getParticipatedEvents(userId);
    }
}
