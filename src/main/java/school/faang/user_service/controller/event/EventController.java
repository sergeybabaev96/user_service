package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final EventMapper eventMapper;

    @PostMapping
    public EventDto create(@Valid @RequestBody EventDto eventDto) {
        Event event  = eventService.create(eventMapper.toEntity(eventDto));
        return eventMapper.toDto(event);
    }

    @GetMapping("/{eventId}")
    public EventDto getEvent(@PathVariable long eventId) {
        Event event = eventService.getEvent(eventId);
        return eventMapper.toDto(event);
    }

    @DeleteMapping("/{eventId}")
    public void deleteEvent(@PathVariable long eventId) {
        eventService.deleteEvent(eventId);
    }

    @PutMapping
    public EventDto updateEvent(@Valid @RequestBody EventDto eventDto) {
        Event updatedEvent = eventService.updateEvent(eventMapper.toEntity(eventDto));
        return eventMapper.toDto(updatedEvent);
    }

    @GetMapping("/owned/{userId}")
    public List<EventDto> getOwnedEvents(@PathVariable long userId) {
        return eventService.getOwnedEvents(userId).stream()
                .map(eventMapper::toDto)
                .toList();
    }

    @GetMapping("/participated/{userId}")
    public List<EventDto> getParticipatedEvents(@PathVariable long userId) {
        return eventService.getParticipatedEvents(userId).stream()
                .map(eventMapper::toDto)
                .toList();
    }

    @PostMapping("/filter")
    public List<EventDto> getEventsByFilter(@RequestBody EventFilterDto filter) {
        return eventService.getEventsByFilter(filter).stream()
                .map(eventMapper::toDto)
                .toList();
    }
}
