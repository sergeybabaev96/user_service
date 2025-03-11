package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.filter.EventFilterDto;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.validator.EventDtoValidator;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PostMapping
    public ResponseEntity<EventDto> create(@RequestBody EventDto event) {
        EventDtoValidator.validate(event);
        EventDto createdEvent = eventService.create(event);

        URI location = URI.create("/events/" + createdEvent.getId());

        return ResponseEntity.created(location).body(createdEvent);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventDto> getEvent(@PathVariable("eventId") long eventId) {
        return ResponseEntity.ok(eventService.getEvent(eventId));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<EventDto>> getEventsByFilter(@ModelAttribute EventFilterDto filter) {
        return ResponseEntity.ok(eventService.getEventsByFilter(filter));
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable("eventId") long eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<EventDto> updateEvent(@PathVariable("eventId") long eventId, @RequestBody EventDto event) {
        EventDtoValidator.validate(event);
        return ResponseEntity.ok(eventService.updateEvent(event,eventId));
    }

    @GetMapping("/owned/{userId}")
    public ResponseEntity<List<EventDto>> getOwnedEvents(@PathVariable("userId") long userId) {
        return ResponseEntity.ok(eventService.getOwnedEvents(userId));
    }

    @GetMapping("/participated/{userId}")
    public ResponseEntity<List<EventDto>> getParticipatedEvents(@PathVariable("userId") long userId) {
        return ResponseEntity.ok(eventService.getParticipatedEvents(userId));
    }
}
