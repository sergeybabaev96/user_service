package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFiltersDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/events")
@RestController
public class EventController {
    private final EventService eventService;

    private final EventMapper eventMapper;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public EventDto create(@Valid @RequestBody EventDto eventDto) {
        Event inputEvent = eventMapper.toEntity(eventDto);

        Event createdEvent = eventService.create(inputEvent, eventDto.ownerId(), eventDto.relatedSkillIds());
        return eventMapper.toDto(createdEvent);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public EventDto getEvent(@PathVariable("id") long id) {
        Event event = eventService.getEvent(id);
        return eventMapper.toDto(event);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/filter")
    public List<EventDto> getEventsByFilter(@RequestBody EventFiltersDto filters) {
        List<Event> events = eventService.getEventsByFilter(filters);
        return eventMapper.toDtoList(events);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{eventId}")
    public void deleteEvent(
            @PathVariable long eventId) {
        eventService.deleteEvent(eventId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/update")
    public EventDto updateEvent(
            @Valid @RequestBody EventDto eventDto,
            @RequestParam Long ownerId,
            @RequestParam List<Long> relatedSkillIds) {
        Event inputEvent = eventMapper.toEntity(eventDto);

        eventService.updateEvent(inputEvent, ownerId, relatedSkillIds);
        return eventMapper.toDto(inputEvent);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/owned")
    public List<EventDto> getOwnedEvents(@RequestParam long userId) {
        List<Event> ownedEvents = eventService.getOwnedEvents(userId);
        return eventMapper.toDtoList(ownedEvents);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/participated")
    public List<EventDto> getParticipatedEvents(@RequestParam long userId) {
        List<Event> participatedEvents = eventService.getParticipatedEvents(userId);
        return eventMapper.toDtoList(participatedEvents);
    }
}
