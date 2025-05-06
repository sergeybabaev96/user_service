package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.EventDto;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@Validated
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PostMapping(value = "/create")
    public EventDto create(@Valid @RequestBody EventDto event) {
        return eventService.create(event);
    }

    @GetMapping(value = "/get")
    public EventDto getEvent(@Valid @RequestBody long eventId) {
        return eventService.getEvent(eventId);
    }

    @GetMapping(value = "/get-by-filter")
    public List<EventDto> getEventsByFilter(@Valid @RequestBody EventFilterDto filter) {
        return eventService.getEventByFilter(filter);
    }
}
