package school.faang.user_service.controller.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDTO;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

@RestController
@RequestMapping("api/v1/event")
public class EventController {
    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/create")
    public EventDTO create(EventDTO eventDTO) {
        try {
            eventService.isValid(eventDTO);
            eventService.create(eventDTO);
            return eventDTO;
        } catch (DataValidationException e) {

            throw new DataValidationException(e.getMessage());
        }
    }
}
