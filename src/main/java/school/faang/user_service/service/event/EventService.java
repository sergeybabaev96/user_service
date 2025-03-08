package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.EventFilterDto;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validator.EventDtoValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final EventMapper eventMapper;

    private static final String EVENT_NOT_FOUND_LOG = "Event with id {} not found. Requested at: {} in method: {}";
    private static final String EVENT_NOT_FOUND_EXCEPTION = "Event with id %s not found";

    public EventDto create(EventDto eventDto) {
        User owner = EventDtoValidator.validateOwnerAndSkills(eventDto, userRepository);
        Event entity = eventMapper.toEntity(eventDto);
        entity.setOwner(owner);
        entity = eventRepository.save(entity);

        return eventMapper.toDto(entity);
    }

    public EventDto getEvent(long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.error(EVENT_NOT_FOUND_LOG, eventId, LocalDateTime.now(), "getEvent");
                    return new DataValidationException(String.format(EVENT_NOT_FOUND_EXCEPTION, eventId));
                });
        return eventMapper.toDto(event);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        List<Event> events = eventRepository.findAll();

        return events.stream()
                .filter(filter::matches)
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
    }

    public void deleteEvent(long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.error(EVENT_NOT_FOUND_LOG, eventId, LocalDateTime.now(), "deleteEvent");
                    return new DataValidationException(String.format(EVENT_NOT_FOUND_EXCEPTION, eventId));
                });
        eventRepository.delete(event);
    }

    public EventDto updateEvent(EventDto eventDto) {
        Event existingEvent = eventRepository.findById(eventDto.getId())
                .orElseThrow(() -> {
                    log.error(EVENT_NOT_FOUND_LOG, eventDto.getId(), LocalDateTime.now(), "updateEvent");
                    return new DataValidationException(
                            String.format(EVENT_NOT_FOUND_EXCEPTION, eventDto.getId()));
                        }
                );

        EventDtoValidator.validateOwnerAndSkills(eventDto, userRepository);
        existingEvent.setRelatedSkills(EventDtoValidator.validateAndGetRelatedSkills(eventDto, skillRepository));

        eventMapper.updateEventFormDto(eventDto, existingEvent);
        existingEvent = eventRepository.save(existingEvent);

        return eventMapper.toDto(existingEvent);
    }

    public List<EventDto> getOwnedEvents(long userId) {
        List<Event> events = eventRepository.findAllByUserId(userId);

        return events.stream()
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        List<Event> events = eventRepository.findParticipatedEventsByUserId(userId);

        return events.stream()
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
    }
}
