package school.faang.user_service.controller.event;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.event.EventDTO;
import school.faang.user_service.dto.event.EventFilterDTO;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/events")
@Tag(name = "Event API", description = "API для управления событиями")
public class EventController {

    private final EventService eventService;

    @PostMapping
    @Operation(summary = "Создание события", description = "Создает новое событие, на основе переданных данных")
    public EventDTO create(@RequestBody EventDTO eventDTO) {
        return eventService.create(eventDTO);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Найти событие", description = "Выводит событие по переданному идентификатору")
    public EventDTO getEvent(@Parameter(description = "Идентификатор события") @PathVariable Long id) {
        return eventService.getById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить событие",
            description = "Находит событие по переданному идентификатору и обновляет, на основе переданных данных")
    public EventDTO update(@Parameter(description = "Идентификатор события")
                               @PathVariable Long id, @RequestBody EventDTO eventDTO) {
            return eventService.update(id, eventDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить событие", description = "Находит событие по переданному идентификатору и удаляет его")
    public void delete(@Parameter(description = "Идентификатор события") @PathVariable Long id) {
        eventService.delete(id);
    }

    @GetMapping("/ownedEvents/{ownerId}")
    @Operation(summary = "Найти события по создателю",
            description = "Находит все события, которые создал пользователь с переданным идентификатором")
    public List<EventDTO> getOwnedEvents(@Parameter(description = "Идентификатор создателя")
                                             @PathVariable Long ownerId) {
        return eventService.getOwnedEvents(ownerId);
    }

    @GetMapping("/participatedEvents/{userId}")
    @Operation(summary = "Найти события по участнику",
            description = "Находит все события, в которых участвует пользователь с переданным идентификатором")
    public List<EventDTO> getParticipatedEvents(@Parameter(description = "Идентификатор участника")
                                                    @PathVariable Long userId) {
        return eventService.getParticipatedEvents(userId);
    }

    @PostMapping("/filter")
    @Operation(summary = "Найти события по фильтру",
            description = "Находит все события, по переданному фильтру")
    public List<EventDTO> getEventsByFilter(@RequestBody EventFilterDTO filter) {
        return eventService.getEventsByFilter(filter);
    }
}
