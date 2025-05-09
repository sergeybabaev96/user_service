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

    @PostMapping("/")
    public EventDto create(@RequestBody EventDto event) {
        validateEventDto(event);
        return eventService.create(event);
    }

    @GetMapping("/{id}/")
    public EventDto getEvent(@PathVariable(name = "id") long id) {
        return eventService.getEvent(id);
    }

    @PostMapping("/partial/")
    public List<EventDto> getEventsByFilter(@RequestBody EventFilterDto eventFilterDto) {
        return eventService.getEventsByFilter(eventFilterDto);
    }

    @DeleteMapping("/{id}/")
    public void deleteEvent(@PathVariable(name = "id") long eventId) {
        eventService.deleteEvent(eventId);
    }

    @PatchMapping("/{id}/")
    public EventDto updateEvent(@RequestBody EventDto event) {
        validateEventDto(event);

        return eventService.updateEvent(event);
    }

    @GetMapping("/owner/{id}/")
    public List<EventDto> getOwnedEvents(@PathVariable(name = "id") long userId) {
        return eventService.getOwnedEvents(userId);
    }

    @GetMapping("/participant/{id}/")
    public List<EventDto> getParticipatedEvents(@PathVariable(name = "id") long userId) {
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
