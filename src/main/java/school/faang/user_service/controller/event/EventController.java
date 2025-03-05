package school.faang.user_service.controller.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.event.EventDTO;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@RestController
@RequestMapping("/event")
public class EventController {
    private final EventService eventService;
    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }
    @PostMapping("/create")
    public EventDTO create(@RequestBody EventDTO eventDTO) {
        try {
            eventService.create(eventDTO);
            return eventDTO;
        } catch (DataValidationException e) {
            throw new DataValidationException(e.getMessage());
        }
    }
    @GetMapping("/get/{id}")
    public EventDTO getEvent(@PathVariable Long id) {
        return eventService.getById(id);
    }
    @PutMapping("/update/{id}")
    public EventDTO update(@PathVariable Long id, @RequestBody EventDTO eventDTO) {
        try{
            return eventService.update(id, eventDTO);
        } catch (DataValidationException e) {
            throw new DataValidationException(e.getMessage());
        }
    }
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        eventService.delete(id);
    }
    @GetMapping("/ownedEvents/{ownerId}")
    public List<EventDTO> getOwnedEvents(@PathVariable Long ownerId) {
        return eventService.getOwnedEvents(ownerId);
    }
    @GetMapping("/participatedEvents/{userId}")
    public List<EventDTO> getParticipatedEvents(@PathVariable Long userId) {
        return eventService.getParticipatedEvents(userId);
    }
}
