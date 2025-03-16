package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventCreateDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventViewDto;
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
     * @return Созданное событие в виде {@link EventViewDto}.
     * @throws DataValidationException если пользователь не обладает необходимыми навыками.
     */
    public EventViewDto create(EventCreateDto eventDto) {
        validateUserSkills(eventDto);
        Event event = eventMapper.toEntity(eventDto);
        User user = getUserById(eventDto.getOwnerId());
        event.setOwner(user);
        List<Skill> relatedSkills = mapSkillIdsToEntities(eventDto.getRelatedSkillsId());
        event.setRelatedSkills(relatedSkills);
        Event savedEvent = eventRepository.save(event);
        return eventMapper.toDto(savedEvent);
    }

    /**
     * Получает событие по его идентификатору.
     *
     * @param eventId Идентификатор события.
     * @return Событие в виде {@link EventViewDto}.
     * @throws DataValidationException если событие не найдено.
     */
    public EventViewDto getEvent(long eventId) {
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
     * @return Список событий в виде {@link List<EventViewDto>}
     */
    public List<EventViewDto> getEventsByFilter(EventFilterDto eventFilterDto) {
        if (eventFilterDto == null) {
            eventFilterDto = new EventFilterDto();
        }

        var events = eventRepository.findAll().stream();

        for (int i = 0; i < eventFilters.size(); i++) {
            var eventFilter = eventFilters.get(i);
            if (eventFilter.isApplicable(eventFilterDto)) {
                events = eventFilter.apply(events, eventFilterDto);
            }
        }

        return events
                .map(eventMapper::toDto)
                .toList();
    }

    /**
     * Удаляет событие по его идентификатору.
     *
     * @param eventId Идентификатор события для удаления.
     * @throws DataValidationException если событие не найдено.
     */
    public void deleteEvent(long eventId) {
        if (!eventRepository.existsById(eventId)) {
            log.error("Event not found with ID: {}", eventId);
            throw new DataValidationException("Event not found with ID: " + eventId);
        }

        eventRepository.deleteById(eventId);
    }

    /**
     * Обновляет существующее событие на основе переданного DTO.
     *
     * @param eventDto DTO события, содержащее обновленные данные.
     * @return Обновленное событие в виде {@link EventViewDto}.
     * @throws DataValidationException если событие не прошло валидацию.
     */
    public EventViewDto updateEvent(EventCreateDto eventDto) {
        Event event = eventMapper.toEntity(eventDto);
        verifyUserEventAccess(event);
        Event updatedEvent = eventRepository.save(event);
        return eventMapper.toDto(updatedEvent);
    }

    /**
     * Получает список событий, созданных конкретным пользователем.
     *
     * @param userId Идентификатор пользователя.
     * @return Список событий в виде {@link List<EventViewDto>}.
     */
    public List<EventViewDto> getOwnerEvent(long userId) {
        return eventRepository.findAllByUserId(userId)
                .stream()
                .map(eventMapper::toDto)
                .toList();
    }

    /**
     * Получает список событий, в которых участвовал конкретный пользователь.
     *
     * @param userId Идентификатор пользователя.
     * @return Список событий в виде {@link List<EventViewDto>}.
     */
    public List<EventViewDto> getParticipatedEvents(long userId) {
        return eventRepository.findParticipatedEventsByUserId(userId)
                .stream()
                .map(eventMapper::toDto)
                .toList();
    }

    /**
     * Преобразует список идентификаторов навыков в список сущностей {@link Skill}.
     *
     * @param skillIds Список идентификаторов навыков.
     * @return Список сущностей {@link Skill}.
     * @throws DataValidationException если навык не найден.
     */
    private List<Skill> mapSkillIdsToEntities(List<Long> skillIds) {
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
    private void verifyUserEventAccess(Event event) {
        User owner = event.getOwner();
        List<Skill> skillsOwner = owner.getSkills();

        boolean hasAccess = skillsOwner.stream()
                .anyMatch(skill -> skill.getEvents().contains(event));

        if (!hasAccess) {
            log.error("User cannot carry out this event: {}", event);
            throw new DataValidationException("User cannot carry out this event - " + event);
        }
    }

    /**
     * Проверяет, обладает ли пользователь необходимыми навыками для создания события.
     *
     * @param eventDto Объект {@link EventCreateDto}, содержащий информацию о создаваемом событии.
     * @throws DataValidationException если пользователь не обладает необходимыми навыками.
     */
    private void validateUserSkills(EventCreateDto eventDto) {
        List<Long> relatedSkillsIds = eventDto.getRelatedSkillsId();

        if (relatedSkillsIds == null || relatedSkillsIds.isEmpty()) {
            log.error("User does not possess required skills for event: {}", eventDto);
            throw new DataValidationException("User does not possess required skills.");
        }

        User user = userRepository.findById(eventDto.getOwnerId())
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", eventDto.getOwnerId());
                    return new DataValidationException("User not found with ID: " + eventDto.getOwnerId());
                });

        List<Skill> relatedSkills = skillRepository.findAllById(relatedSkillsIds);

        if (user.getSkills().retainAll(relatedSkills)) {
            log.error("User does not possess required skills for event: {}", eventDto);
            throw new DataValidationException("User does not possess required skills.");
        }
    }
}