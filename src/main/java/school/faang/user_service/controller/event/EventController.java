package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.service.event.EventService;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PostMapping
    public EventDto create(@RequestBody EventDto event) {
        validateEvent(event);
        return eventService.create(event);
    }

    private void validateEvent(EventDto event) {
        if (event.getTitle() == null || event.getTitle().isBlank()) {
            throw new DataValidationException("Название события обязательно");
        }
        if (event.getStartDate() == null) {
            throw new DataValidationException("Дата начала события обязательна");
        }
        if (event.getOwnerId() == null) {
            throw new DataValidationException("Не указан владелец события");
        }
    }
}

