package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.exeption.DataValidationException;
import school.faang.user_service.service.EventService;
import school.faang.user_service.validator.EventValidator;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final List<EventValidator> eventValidators;

    @PostMapping(value={"", "/"})
    public EventDto create(@RequestBody EventDto event) {
        validateEventDto(event);
        return eventService.create(event);
    }

    @GetMapping(value={"/{eventId}", "/{eventId}/"})
    public EventDto getEvent(@PathVariable("eventId") long eventId) {
        return eventService.getEvent(eventId);
    }

    @PostMapping(value={"/partial", "/partial/"})
    public List<EventDto> getEventsByFilter(@RequestBody EventFilterDto eventFilterDto) {
        return eventService.getEventsByFilter(eventFilterDto);
    }

    @DeleteMapping(value={"/{eventId}", "/{eventId}/"})
    public void deleteEvent(@PathVariable("eventId") long eventId) {
        eventService.deleteEvent(eventId);
    }

    @PatchMapping(value={"/{eventId}", "/{eventId}/"})
    public EventDto updateEvent(@RequestBody EventDto event) {
        validateEventDto(event);

        return eventService.updateEvent(event);
    }

    @GetMapping(value={"/owner/{userId}", "/owner/{userId}/"})
    public List<EventDto> getOwnedEvents(@PathVariable("userId") long userId) {
        return eventService.getOwnedEvents(userId);
    }

    @GetMapping(value={"/participant/{userId}", "/participant/{userId}/"})
    public List<EventDto> getParticipatedEvents(@PathVariable("userId") long userId) {
        return eventService.getParticipatedEvents(userId);
    }

    private void validateEventDto(EventDto event) {
        if (eventValidators.stream()
                .allMatch(validator -> validator.validate(event))
        ) {
            return;
        }

        throw new DataValidationException("Event data is invalid");
    }
}
