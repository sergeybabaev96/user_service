package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.List;

import static school.faang.user_service.utils.ValidationUtils.validateEvent;
import static school.faang.user_service.utils.ValidationUtils.validateEventId;

@Controller
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    public EventDto create(EventDto event) {
        validateEvent(event);
        return eventService.create(event);
    }

    public EventDto getEvent(Long id) {
        validateEventId(id);
        return eventService.getEvent(id);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto eventFilter) {
        return eventService.getEventsByFilter(eventFilter);}
    private void validateEvent(EventDto event) {
        if (event == null || event.getTitle().isBlank() || !Objects.nonNull(event.getStartDate())
                || event.getStartDate().isBefore(LocalDateTime.now())
                || event.getOwnerId() == null ) {
            throw new DataValidationException("Event not confirmed");
        }
    }

    public void deleteEvent(Long id) {
        validateEventId(id);
        eventService.deleteEvent(id);
    }

    public EventDto updateEvent(EventDto eventDto) {
        validateEvent(eventDto);
        return eventService.updateEvent(eventDto);
    }

    public List<EventDto> getParticipatedEvents(Long userId) {
        return eventService.getParticipatedEvents(userId);
    }



    private void validateEventId(Long id) {
        if(id == null || id <= 0) {
            throw new DataValidationException("Event id not valid");
        }
    }

}
