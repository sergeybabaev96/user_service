package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.service.EventServiceImpl;

import java.util.List;

import static school.faang.user_service.utils.ValidationUtils.validateEvent;
import static school.faang.user_service.utils.ValidationUtils.validateEventId;

@Controller
@RequiredArgsConstructor
public class EventController {
    private final EventServiceImpl eventService;

    public EventDto create(EventDto eventDto) {
        validateEvent(eventDto);
        return eventService.create(eventDto);
    }

    public EventDto getEvent(Long id) {
        validateEventId(id);
        return eventService.getEvent(id);
    }

    public List<EventDto> getEventsByFilter(@Valid EventFilterDto eventFilter) {
        return eventService.getEventsByFilter(eventFilter);
    }

    public void deleteEvent(Long id) {
        validateEventId(id);
        eventService.deleteEvent(id);
    }

    public EventDto updateEvent(@Valid EventDto eventDto) {
        validateEvent(eventDto);
        return eventService.updateEvent(eventDto);
    }

    public List<EventDto> getParticipatedEvents(Long userId) {
        return eventService.getParticipatedEvents(userId);
    }


}
