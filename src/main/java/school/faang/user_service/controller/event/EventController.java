package school.faang.user_service.controller.event;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventCreateDto;
import school.faang.user_service.dto.event.EventViewDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/events")
@Validated
@RequiredArgsConstructor
@Tag(name = "Event Management", description = "Provides methods for working with events")
public class EventController {
    private final EventService eventService;

    @Operation(
            summary = "Create new event",
            description = "Create new event with specified data"
    )
    @PostMapping
    public EventViewDto create(@Valid @RequestBody EventCreateDto event) {
        log.info("Creating event with title: {}", event.getTitle());
        EventViewDto createdEvent = eventService.create(event);
        log.info("Event created successfully with ID: {}", createdEvent.getId());
        return createdEvent;
    }

    @Operation(
            summary = "Get event by ID",
            description = "Get event by specified ID"
    )
    @GetMapping("/{eventId}")
    public EventViewDto getEvent(
            @Parameter(description = "ID of the event to retrieve", required = true, example = "1")
            @PathVariable("eventId") long eventId) {
        log.info("Fetching event with ID: {}", eventId);
        EventViewDto event = eventService.getEvent(eventId);
        log.info("Event fetched successfully: {}", event);
        return event;
    }

    @Operation(
            summary = "Get events by filter",
            description = "Get events by specified filter"
    )
    @GetMapping("/filter")
    public List<EventViewDto> getEvents(@Valid @ModelAttribute EventFilterDto filter) {
        log.info("Fetching events with filter: {}", filter);
        List<EventViewDto> events = eventService.getEventsByFilter(filter);
        log.info("Fetched {} events", events.size());
        return events;
    }

    @Operation(
            summary = "Delete event",
            description = "Delete event by specified ID"
    )
    @DeleteMapping("/{eventId}")
    public void deleteEvent(
            @Parameter(description = "ID of the event to delete", required = true, example = "1")
            @PathVariable("eventId") long eventId) {
        log.info("Deleting event with ID: {}", eventId);
        eventService.deleteEvent(eventId);
        log.info("Event deleted successfully");
    }

    @Operation(
            summary = "Update an existing event",
            description = "Update the event with the specified ID"
    )
    @PutMapping("/{eventId}")
    public EventViewDto updateEvent(
            @Parameter(description = "ID of the event to update", required = true, example = "1")
            @PathVariable("eventId") long eventId, @Valid @RequestBody EventCreateDto event) {
        log.info("Updating event with ID: {}", eventId);
        EventViewDto updatedEvent = eventService.updateEvent(eventId, event);
        log.info("Event updated successfully: {}", updatedEvent);
        return updatedEvent;
    }

    @Operation(
            summary = "Get events by owner ID",
            description = "Get events created by the specified owner ID"
    )
    @GetMapping("/owner/{userId}")
    public List<EventViewDto> getOwnerEvent(
            @Parameter(description = "ID of the owner (user) to filter events", required = true, example = "123")
            @PathVariable("userId") long userId) {
        log.info("Fetching events for owner with ID: {}", userId);
        List<EventViewDto> events = eventService.getOwnerEvent(userId);
        log.info("Fetched {} events for owner", events.size());
        return events;
    }

    @Operation(
            summary = "Get events by participant ID",
            description = "Get events participated by the specified participant ID"
    )
    @GetMapping("/participated/{userId}")
    public List<EventViewDto> getParticipatedEvents(
            @Parameter(description = "ID of the participant (user) to filter events", required = true, example = "123")
            @PathVariable("userId") long userId) {
        log.info("Fetching events participated by user with ID: {}", userId);
        List<EventViewDto> events = eventService.getParticipatedEvents(userId);
        log.info("Fetched {} participated events", events.size());
        return events;
    }
}