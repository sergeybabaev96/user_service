package school.faang.user_service.controller.event;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.EventDto;
import school.faang.user_service.dto.EventFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.validation.data.Required;

import java.util.List;

@Validated
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PostMapping(value = "/create")
    public EventDto create(@Valid @RequestBody EventDto event) {
        isValidDataRange(event);
        return eventService.create(event);
    }

    @GetMapping(value = "/get")
    public EventDto getEvent(@RequestBody @Required Long eventId) {
        return eventService.getEvent(eventId);
    }

    @GetMapping(value = "/get/filter")
    public List<EventDto> getEventsByFilter(@RequestBody EventFilterDto filter) {
        return eventService.getEventsByFilter(filter);
    }

    @DeleteMapping(value = "/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@Required Long eventId) {
        eventService.deleteEvent(eventId);
    }

    @PutMapping(value = "/update")
    public EventDto updateEvent(@RequestBody EventDto eventDto) {
        isValidDataRange(eventDto);
        return eventService.updateEvent(eventDto);
    }

    @GetMapping(value = "/owned")
    public List<EventDto> getOwnedEvents(@Required Long userId) {
        return eventService.getOwnedEvents(userId);
    }

    @GetMapping(value = "/participated")
    public List<EventDto> getParticipatedEvents(@Required Long userId) {
        return eventService.getParticipatedEvents(userId);
    }

    private void isValidDataRange(EventDto event) {
        if (event.getEndDate() != null) {
            if (event.getStartDate().isBefore(event.getEndDate())) {
            } else {
                throw new DataValidationException("The end date must be after the start date");
            }
        }
    }
}
