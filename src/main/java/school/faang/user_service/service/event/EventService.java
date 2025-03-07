package school.faang.user_service.service.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDTO;
import school.faang.user_service.dto.event.EventFilterDTO;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final EventUtil eventUtil = new EventUtil();

    @Autowired
    public EventService(EventRepository eventRepository1, UserRepository userRepository, EventMapper eventMapper) {
        this.eventRepository = eventRepository1;
        this.userRepository = userRepository;
        this.eventMapper = eventMapper;
    }

    public EventDTO create(EventDTO event) {
        User eventOwner = userRepository.findById(event.getOwnerId())
                .orElseThrow(() -> new DataValidationException("Owner not found"));
        if (eventUtil.isValid(event) && eventUtil.checkOwnerSkills(eventOwner, event)) {
            Event entityEvent = eventMapper.eventDTOToEvent(event);
            //убеждаемся что владелец точно установился для формирования правильных связей в БД.
            entityEvent.setOwner(userRepository.findById(event.getOwnerId())
                    .orElseThrow(() -> new DataValidationException("Owner not found")));
            eventRepository.save(entityEvent);
            return eventMapper.eventToEventDTO(entityEvent);
        } else {
            throw new DataValidationException("Skills are empty or not match");
        }
    }

    public EventDTO getById(Long id) {
        return eventMapper.eventToEventDTO(eventRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Event not found")));
    }

    public EventDTO update(Long eventId, EventDTO event) {
        // Получаем существующее событие
        Event updatedEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new DataValidationException("Event not found"));
        // Проверяем, что переданный DTO действительно соответствует обновляемой сущности
        if (!eventId.equals(event.getId())) {
            throw new DataValidationException("Mismatched event ID");
        }
        User eventOwner = userRepository.findById(event.getOwnerId())
                .orElseThrow(() -> new DataValidationException("Owner not found"));
        if (!eventUtil.checkOwnerSkills(eventOwner, event) || !eventUtil.isValid(event)) {
            throw new DataValidationException("Skills are empty or not match");
        }
        // Используем маппер для обновления существующего объекта
        eventMapper.updateEventFromDTO(event, updatedEvent);
        // Устанавливаем владельца, так как маппер его игнорирует
        updatedEvent.setOwner(eventOwner);
        // Сохраняем обновленное событие
        eventRepository.save(updatedEvent);
        return eventMapper.eventToEventDTO(updatedEvent);
    }

    public void delete(Long id) {
        var wasDeletedEvent = eventRepository.findById(id).orElseThrow(()
                -> new DataValidationException("Event not found"));
        log.info("Event deleted: {}", wasDeletedEvent);
        eventRepository.delete(wasDeletedEvent);
    }

    public List<EventDTO> getOwnedEvents(Long ownerId) {
        List<Event> userEvents = eventRepository.findAllByUserId(ownerId);
        return eventMapper.eventsToEventDTOs(userEvents);
    }
    public List<EventDTO> getParticipatedEvents(Long userId) {
        List<Event> userEvents = eventRepository.findParticipatedEventsByUserId(userId);
        return eventMapper.eventsToEventDTOs(userEvents);
    }
    public List<EventDTO> getEventsByFilter(EventFilterDTO filter) {
        List<Event> eventsList = eventRepository.findAll();
        Stream<EventDTO> filteredEventDTOs = eventMapper.eventsToEventDTOs(eventsList).stream()
                .filter(eventDTO ->
                        eventDTO.getLocation()
                                .equals(filter.getLocation()));
        return filteredEventDTOs.toList();
    }
}
