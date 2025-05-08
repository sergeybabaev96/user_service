package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final SkillRepository skillRepository;
    private final EventMapper eventMapper;

    public EventDto create(EventDto event) {
        List<Long> userSkills = skillRepository.findAllByUserId(event.getOwnerId())
                .stream().map(Skill::getId)
                .toList();

        boolean hasAllSkills = userSkills.containsAll(event.getRelatedSkills());
        if (!hasAllSkills) {
            log.warn("");
            throw new DataValidationException("Пользователь не обладает всеми требуемыми навыками");
        }

        Event saved = eventRepository.save(eventMapper.toEntity(event));
        return eventMapper.toDto(saved);
    }

    public EventDto getEvent(long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.warn("Событие с id={} не найдено", eventId);
                    return new DataValidationException("Событие с id=" + eventId + " не найдено");
                });

        return eventMapper.toDto(event);
    }

    public void deleteEvent(long eventId) {
        if (!eventRepository.existsById(eventId)) {
            log.warn("Попытка удалить несуществующее событие с id={}", eventId);
            throw new DataValidationException("Событие с id=" + eventId + " не найдено и не может быть удалено");
        }

        eventRepository.deleteById(eventId);
        log.info("Событие с id={} успешно удалено", eventId);
    }

    public EventDto updateEvent(EventDto eventDto) {
        Event existingEvent = eventRepository.findById(eventDto.getId())
                .orElseThrow(() -> new DataValidationException("Событие с id=" + eventDto.getId() + " не найдено"));

        if (!Objects.equals(existingEvent.getOwner(), eventDto.getOwnerId())) {
            log.warn("Попытка обновить событие пользователем, не являющимся автором");
            throw new DataValidationException("Обновление разрешено только автору события");
        }

        List<Long> userSkills = skillRepository.findAllByUserId(eventDto.getOwnerId())
                .stream().map(Skill::getId)
                .toList();

        if (!userSkills.containsAll(eventDto.getRelatedSkills())) {
            throw new DataValidationException("Пользователь не обладает всеми навыками, указанными в обновлении");
        }

        Event updatedEvent = eventRepository.save(eventMapper.toEntity(eventDto));
        log.info("Событие с id={} успешно обновлено", updatedEvent.getId());
        return eventMapper.toDto(updatedEvent);
    }

    public List<EventDto> getOwnedEvents(long userId) {
        return eventRepository.findAllByUserId(userId)
                .stream()
                .map(eventMapper::toDto)
                .toList();
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        return eventRepository.findParticipatedEventsByUserId(userId)
                .stream()
                .map(eventMapper::toDto)
                .toList();
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        return eventRepository.findAll().stream()
                .filter(event -> filter.getTitle() == null || event.getTitle().toLowerCase().contains(filter.getTitle().toLowerCase()))
                .filter(event -> filter.getOwnerId() == null || Objects.equals(event.getOwner(), filter.getOwnerId()))
                .filter(event -> filter.getEventType() == null || event.getType() == filter.getEventType())
                .filter(event -> filter.getEventStatus() == null || event.getStatus() == filter.getEventStatus())
                .filter(event -> filter.getStartFrom() == null || (event.getStartDate() != null && !event.getStartDate().isBefore(filter.getStartFrom())))
                .filter(event -> filter.getStartTo() == null || (event.getStartDate() != null && !event.getStartDate().isAfter(filter.getStartTo())))
                .map(eventMapper::toDto)
                .toList();
    }
}
