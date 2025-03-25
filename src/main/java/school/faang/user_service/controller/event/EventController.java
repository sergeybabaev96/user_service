package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventCreateDto;
import school.faang.user_service.dto.event.EventViewDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.service.event.EventService;

import java.util.List;

/**
 * Контроллер для управления событиями.
 * Предоставляет методы для создания, получения, обновления и удаления событий,
 * а также для получения событий по фильтрам и для конкретных пользователей.
 *
 * @author Zhltsk-V
 */
@Slf4j
@RestController
@RequestMapping("/events")
@Validated
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    /**
     * Создает новое событие.
     *
     * @param event DTO события, содержащее данные для создания.
     * @return Созданное событие в виде {@link EventViewDto}.
     */
    @PostMapping
    public EventViewDto create(@Valid @RequestBody EventCreateDto event) {
        log.info("Creating event with title: {}", event.getTitle());
        EventViewDto createdEvent = eventService.create(event);
        log.info("Event created successfully with ID: {}", createdEvent.getId());
        return createdEvent;
    }

    /**
     * Получает событие по его идентификатору.
     *
     * @param eventId Идентификатор события.
     * @return Событие в виде {@link EventViewDto}.
     */
    @GetMapping("/{eventId}")
    public EventViewDto getEvent(@PathVariable("eventId") long eventId) {
        log.info("Fetching event with ID: {}", eventId);
        EventViewDto event = eventService.getEvent(eventId);
        log.info("Event fetched successfully: {}", event);
        return event;
    }

    /**
     * Получает список событий, соответствующих заданному фильтру.
     *
     * @param filter DTO фильтра, содержащее критерии поиска.
     * @return Список событий в виде {@link List<EventViewDto>}.
     */
    @GetMapping("/filter")
    public List<EventViewDto> getEvents(@Valid @ModelAttribute EventFilterDto filter) {
        log.info("Fetching events with filter: {}", filter);
        List<EventViewDto> events = eventService.getEventsByFilter(filter);
        log.info("Fetched {} events", events.size());
        return events;
    }

    /**
     * Удаляет событие по его идентификатору.
     *
     * @param eventId Идентификатор события для удаления.
     */
    @DeleteMapping("/{eventId}")
    public void deleteEvent(@PathVariable("eventId") long eventId) {
        log.info("Deleting event with ID: {}", eventId);
        eventService.deleteEvent(eventId);
        log.info("Event deleted successfully");
    }

    /**
     * Обновляет существующее событие.
     *
     * @param eventId Идентификатор события для обновления.
     * @param event DTO события, содержащее обновленные данные.
     * @return Обновленное событие в виде {@link EventViewDto}.
     */
    @PutMapping("/{eventId}")
    public EventViewDto updateEvent(@PathVariable("eventId") long eventId, @Valid @RequestBody EventCreateDto event) {
        log.info("Updating event with ID: {}", eventId);
        EventViewDto updatedEvent = eventService.updateEvent(eventId, event);
        log.info("Event updated successfully: {}", updatedEvent);
        return updatedEvent;
    }

    /**
     * Получает список событий, созданных конкретным пользователем.
     *
     * @param userId Идентификатор пользователя.
     * @return Список событий в виде {@link List<EventViewDto>}.
     */
    @GetMapping("/owner/{userId}")
    public List<EventViewDto> getOwnerEvent(@PathVariable("userId") long userId) {
        log.info("Fetching events for owner with ID: {}", userId);
        List<EventViewDto> events = eventService.getOwnerEvent(userId);
        log.info("Fetched {} events for owner", events.size());
        return events;
    }

    /**
     * Получает список событий, в которых участвовал конкретный пользователь.
     *
     * @param userId Идентификатор пользователя.
     * @return Список событий в виде {@link List<EventViewDto>}.
     */
    @GetMapping("/participated/{userId}")
    public List<EventViewDto> getParticipatedEvents(@PathVariable("userId") long userId) {
        log.info("Fetching events participated by user with ID: {}", userId);
        List<EventViewDto> events = eventService.getParticipatedEvents(userId);
        log.info("Fetched {} participated events", events.size());
        return events;
    }
}