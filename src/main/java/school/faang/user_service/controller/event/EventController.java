package school.faang.user_service.controller.event;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.filter.event.EventFilterDto;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@Tag(name = "События")
@RestController
@RequiredArgsConstructor
@RequestMapping("${domain.path}/events")
public class EventController {
    private final EventService eventService;

    @Operation(summary = "Создать событие")
    @PostMapping
    public EventDto create(@RequestBody @Valid EventDto event) {
        return eventService.create(event);
    }

    @Operation(summary = "Получить событие по идентификатору")
    @GetMapping("/{id}")
    public EventDto getEvent(@PathVariable("id") long id) {
        return eventService.getEvent(id);
    }

    @Operation(summary = "Получить события с фильтрами")
    @PostMapping(value = "/filter")
    public List<EventDto> getEventsByFilter(@RequestBody EventFilterDto filter) {
        return eventService.getEventByFilters(filter);
    }

    @Operation(summary = "Удалить событие")
    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable long id) {
        eventService.deleteEvent(id);
    }

    @Operation(summary = "Обновить событие")
    @PutMapping
    public EventDto updateEvent(@RequestBody EventDto event) {
        return eventService.updateEvent(event);
    }

    @Operation(summary = "Получить события по идентификатору владельца")
    @GetMapping("/by-owned/{userId}")
    public List<EventDto> getOwnedEvents(@PathVariable("userId") long userId) {
        return eventService.getOwnedEvents(userId);
    }

    @Operation(summary = "Получить события по идентификатору участника")
    @GetMapping("/by-owned-participated/{userId}")
    public List<EventDto> getParticipatedEvents(@PathVariable("userId") long userId) {
        return eventService.getParticipatedEvents(userId);
    }
}
