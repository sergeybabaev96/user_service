package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    public EventDto create(EventDto event) {
        validateEvent(event);
        return eventService.create(event);
    }

    private void validateEvent(EventDto event) {
        if (event == null || event.getTitle().isBlank() || !Objects.nonNull(event.getStartDate())
                || event.getStartDate().isBefore(LocalDateTime.now())
                || event.getOwnerId() == null ) {
            throw new DataValidationException("Event not confirmed");
        }
    }
}
