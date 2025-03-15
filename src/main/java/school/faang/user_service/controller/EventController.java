package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    public EventDto create(EventDto eventDto) {
        validateEvent(eventDto);
        return eventService.create(eventDto);
    }

    public EventDto getEvent(Long id) {
        validateEventId(id);
        return eventService.getEvent(id);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto eventFilter) {
        return eventService.getEventsByFilter(eventFilter);
    }

    public void deleteEvent(Long id) {
        validateEventId(id);
        eventService.deleteEvent(id);
    }

    private void validateEvent(EventDto event) {
        if (event == null || event.getTitle().isBlank() || !Objects.nonNull(event.getStartDate())
                || event.getStartDate().isBefore(LocalDateTime.now())
                || event.getOwnerId() == null ) {
            throw new DataValidationException("Event not confirmed");
        }
    }

    private void validateEventId(Long id) {
        if(id == null || id <= 0) {
            throw new DataValidationException("Event id not valid");
        }
    }

}
