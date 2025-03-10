package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.event.EventDTO;
import school.faang.user_service.dto.event.EventFilterDTO;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/event")
public class EventController {

    private final EventService eventService;

    @PostMapping("/create")
    public EventDTO create(@RequestBody EventDTO eventDTO) {
        eventService.create(eventDTO);
        return eventDTO;
    }

    @GetMapping("/get/{id}")
    public EventDTO getEvent(@PathVariable Long id) {
        return eventService.getById(id);
    }

    @PutMapping("/update/{id}")
    public EventDTO update(@PathVariable Long id, @RequestBody EventDTO eventDTO) {
            return eventService.update(id, eventDTO);
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

    @PostMapping("/filter")
    public List<EventDTO> getEventsByFilter(@RequestBody EventFilterDTO filter) {
        return eventService.getEventsByFilter(filter);
    }
}
