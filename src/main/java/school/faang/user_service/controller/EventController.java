package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.service.EventServiceImpl;

import java.util.List;

import static school.faang.user_service.utils.ValidationUtils.validateEvent;
import static school.faang.user_service.utils.ValidationUtils.validateEventId;

@RestController
@RequiredArgsConstructor
public class EventController {
    private final EventServiceImpl eventService;

    @PostMapping
    public EventDto create(@Valid @RequestBody EventDto eventDto) {
        validateEvent(eventDto);
        return eventService.create(eventDto);
    }

    public EventDto getEvent(Long id) {
        validateEventId(id);
        return eventService.getEvent(id);
    }

    @GetMapping
    public List<EventDto> getEventsByFilter(@Valid @RequestBody EventFilterDto eventFilter) {
        return eventService.getEventsByFilter(eventFilter);
    }

    public void deleteEvent(Long id) {
        validateEventId(id);
        eventService.deleteEvent(id);
    }

    @PutMapping
    public EventDto updateEvent(@Valid @RequestBody EventDto eventDto) {
        validateEvent(eventDto);
        return eventService.updateEvent(eventDto);
    }

    public List<EventDto> getParticipatedEvents(Long userId) {
        return eventService.getParticipatedEvents(userId);
    }


}
