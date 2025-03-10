package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import school.faang.user_service.dto.event.EventDto;
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
@Controller
@Validated
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    /**
     * Создает новое событие.
     *
     * @param event DTO события, содержащее данные для создания.
     * @return Созданное событие в виде {@link EventDto}.
     */
    public EventDto create(@Valid EventDto event) {
        log.info("Creating event with title: {}", event.getTitle());
        EventDto createdEvent = eventService.create(event);
        log.info("Event created successfully with ID: {}", createdEvent.getId());
        return createdEvent;
    }

    /**
     * Получает событие по его идентификатору.
     *
     * @param eventId Идентификатор события.
     * @return Событие в виде {@link EventDto}.
     */
    public EventDto getEvent(long eventId) {
        log.info("Fetching event with ID: {}", eventId);
        EventDto event = eventService.getEvent(eventId);
        log.info("Event fetched successfully: {}", event);
        return event;
    }

    /**
     * Получает список событий, соответствующих заданному фильтру.
     *
     * @param filter DTO фильтра, содержащее критерии поиска.
     * @return Список событий в виде {@link List<EventDto>}.
     */
    public List<EventDto> getEventsByFilter(@Valid EventFilterDto filter) {
        log.info("Fetching events with filter: {}", filter);
        List<EventDto> events = eventService.getEventsByFilter(filter);
        log.info("Fetched {} events", events.size());
        return events;
    }

    /**
     * Удаляет событие по его идентификатору.
     *
     * @param eventId Идентификатор события для удаления.
     */
    public void deleteEvent(long eventId) {
        log.info("Deleting event with ID: {}", eventId);
        eventService.deleteEvent(eventId);
        log.info("Event deleted successfully");
    }

    /**
     * Обновляет существующее событие.
     *
     * @param event DTO события, содержащее обновленные данные.
     * @return Обновленное событие в виде {@link EventDto}.
     */
    public EventDto updateEvent(@Valid EventDto event) {
        log.info("Updating event with ID: {}", event.getId());
        EventDto updatedEvent = eventService.updateEvent(event);
        log.info("Event updated successfully: {}", updatedEvent);
        return updatedEvent;
    }

    /**
     * Получает список событий, созданных конкретным пользователем.
     *
     * @param userId Идентификатор пользователя.
     * @return Список событий в виде {@link List<EventDto>}.
     */
    public List<EventDto> getOwnerEvent(long userId) {
        log.info("Fetching events for owner with ID: {}", userId);
        List<EventDto> events = eventService.getOwnerEvent(userId);
        log.info("Fetched {} events for owner", events.size());
        return events;
    }

    /**
     * Получает список событий, в которых участвовал конкретный пользователь.
     *
     * @param userId Идентификатор пользователя.
     * @return Список событий в виде {@link List<EventDto>}.
     */
    public List<EventDto> getParticipatedEvents(long userId) {
        log.info("Fetching events participated by user with ID: {}", userId);
        List<EventDto> events = eventService.getParticipatedEvents(userId);
        log.info("Fetched {} participated events", events.size());
        return events;
    }
}
