package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;
import java.util.stream.Stream;

/**
 * Сервис для управления событиями.
 * Предоставляет методы для создания, получения, обновления и удаления событий,
 * а также для фильтрации событий и получения событий по владельцу или участнику.
 *
 * @author Zhltsk-V
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final EventMapper eventMapper;
    private final List<EventFilter> eventFilters;

    /**
     * Создает новое событие на основе переданного DTO.
     *
     * @param eventDto DTO события, содержащее данные для создания.
     * @return Созданное событие в виде {@link EventDto}.
     * @throws DataValidationException если пользователь не обладает необходимыми навыками.
     */
    public EventDto create(EventDto eventDto) {
        log.info("Creating event with data: {}", eventDto);
        validateUserSkills(eventDto);
        Event event = eventMapper.toEntity(eventDto);
        User user = getUserById(eventDto.getOwnerId());
        event.setOwner(user);
        List<Skill> relatedSkills = mapSkillIdsToEntities(eventDto.getRelatedSkillsId());
        event.setRelatedSkills(relatedSkills);
        Event savedEvent = eventRepository.save(event);
        log.info("Event created successfully with ID: {}", savedEvent.getId());
        return eventMapper.toDto(savedEvent);
    }

    /**
     * Получает событие по его идентификатору.
     *
     * @param eventId Идентификатор события.
     * @return Событие в виде {@link EventDto}.
     * @throws DataValidationException если событие не найдено.
     */
    public EventDto getEvent(long eventId) {
        log.info("Fetching event with ID: {}", eventId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.error("Event not found with ID: {}", eventId);
                    return new DataValidationException("Event not found with ID: " + eventId);
                });
        return eventMapper.toDto(event);
    }

    /**
     * Получает список событий, соответствующих заданному фильтру.
     *
     * @param eventFilterDto DTO фильтра, содержащее критерии поиска.
     * @return Список событий в виде {@link List<EventDto>}.
     */
    public List<EventDto> getEventsByFilter(EventFilterDto eventFilterDto) {
        log.info("Fetching events by filter: {}", eventFilterDto);
        Stream<Event> allEvents = eventRepository.findAll().stream();

        for (EventFilter eventFilter : eventFilters) {
            if (eventFilter.isApplicable(eventFilterDto)) {
                allEvents = eventFilter.apply(allEvents, eventFilterDto);
            }
        }

        List<EventDto> filteredEvents = allEvents
                .map(eventMapper::toDto)
                .toList();
        log.info("Found {} events matching the filter", filteredEvents.size());
        return filteredEvents;
    }

    /**
     * Удаляет событие по его идентификатору.
     *
     * @param eventId Идентификатор события для удаления.
     * @throws DataValidationException если событие не найдено.
     */
    public void deleteEvent(long eventId) {
        log.info("Deleting event with ID: {}", eventId);
        eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.error("Event not found with ID: {}", eventId);
                    return new DataValidationException("Event not found with ID: " + eventId);
                });
        eventRepository.deleteById(eventId);
        log.info("Event deleted successfully with ID: {}", eventId);
    }

    /**
     * Обновляет существующее событие на основе переданного DTO.
     *
     * @param eventDto DTO события, содержащее обновленные данные.
     * @return Обновленное событие в виде {@link EventDto}.
     * @throws DataValidationException если событие не прошло валидацию.
     */
    public EventDto updateEvent(EventDto eventDto) {
        log.info("Updating event with data: {}", eventDto);
        Event event = eventMapper.toEntity(eventDto);
        validation(event);
        Event updatedEvent = eventRepository.save(event);
        log.info("Event updated successfully with ID: {}", updatedEvent.getId());
        return eventMapper.toDto(updatedEvent);
    }

    /**
     * Получает список событий, созданных конкретным пользователем.
     *
     * @param userId Идентификатор пользователя.
     * @return Список событий в виде {@link List<EventDto>}.
     */
    public List<EventDto> getOwnerEvent(long userId) {
        log.info("Fetching events for owner with ID: {}", userId);
        List<Event> events = eventRepository.findAllByUserId(userId);
        List<EventDto> eventDtos = events.stream()
                .map(eventMapper::toDto)
                .toList();
        log.info("Found {} events for owner with ID: {}", eventDtos.size(), userId);
        return eventDtos;
    }

    /**
     * Получает список событий, в которых участвовал конкретный пользователь.
     *
     * @param userId Идентификатор пользователя.
     * @return Список событий в виде {@link List<EventDto>}.
     */
    public List<EventDto> getParticipatedEvents(long userId) {
        log.info("Fetching participated events for user with ID: {}", userId);
        List<Event> events = eventRepository.findParticipatedEventsByUserId(userId);
        List<EventDto> eventDtos = events.stream()
                .map(eventMapper::toDto)
                .toList();
        log.info("Found {} participated events for user with ID: {}", eventDtos.size(), userId);
        return eventDtos;
    }

    /**
     * Преобразует список идентификаторов навыков в список сущностей {@link Skill}.
     *
     * @param skillIds Список идентификаторов навыков.
     * @return Список сущностей {@link Skill}.
     * @throws DataValidationException если навык не найден.
     */
    private List<Skill> mapSkillIdsToEntities(List<Long> skillIds) {
        log.debug("Mapping skill IDs to entities: {}", skillIds);
        return skillIds.stream()
                .map(id -> skillRepository.findById(id)
                        .orElseThrow(() -> {
                            log.error("Skill not found for ID: {}", id);
                            return new DataValidationException("Skill not found for ID: " + id);
                        }))
                .toList();
    }

    /**
     * Получает пользователя по его идентификатору.
     *
     * @param userId Идентификатор пользователя.
     * @return Сущность {@link User}.
     * @throws DataValidationException если пользователь не найден.
     */
    private User getUserById(Long userId) {
        log.debug("Fetching user with ID: {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", userId);
                    return new DataValidationException("User not found with ID: " + userId);
                });
    }

    /**
     * Проверяет, может ли пользователь проводить указанное событие.
     *
     * @param event Событие для валидации.
     * @throws DataValidationException если пользователь не может проводить событие.
     */
    private void validation(Event event) {
        log.debug("Validating event: {}", event);
        User owner = event.getOwner();
        List<Skill> skillsOwner = owner.getSkills();
        for (Skill skill : skillsOwner) {
            if (!skill.getEvents().contains(event)) {
                log.error("User cannot carry out this event: {}", event);
                throw new DataValidationException("User cannot carry out this event - " + event);
            }
        }
    }

    /**
     * Проверяет, обладает ли пользователь необходимыми навыками для создания события.
     *
     * @param eventDto DTO события.
     * @throws DataValidationException если пользователь не обладает необходимыми навыками.
     */
    private void validateUserSkills(EventDto eventDto) {
        log.debug("Validating user skills for event: {}", eventDto);
        List<Long> requiredSkillIds = eventDto.getRelatedSkillsId();

        if (requiredSkillIds == null || requiredSkillIds.isEmpty()) {
            log.debug("No required skills specified for event");
            return;
        }

        User user = userRepository.findById(eventDto.getOwnerId())
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", eventDto.getOwnerId());
                    return new DataValidationException("User not found with ID: " + eventDto.getOwnerId());
                });

        List<Skill> requiredSkills = skillRepository.findAllById(requiredSkillIds);

        if (!user.getSkills().containsAll(requiredSkills)) {
            log.error("User does not possess required skills for event: {}", eventDto);
            throw new DataValidationException("User does not possess required skills.");
        }
    }
}