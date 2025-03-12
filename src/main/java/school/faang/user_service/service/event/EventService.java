package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.Event.*;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final EventMapper eventMapper;

    private static final String EVENT_NOT_FOUND_LOG_DETAIL = "Event with id {} not found";
    private static final String EVENT_NOT_FOUND_LOG = "Event with id";

    private static final String ERROR_USER_NOT_FOUND_DETAIL = "User with id {} not found";
    private static final String ERROR_USER_NOT_FOUND = "User not found";

    private static final String ERROR_SKILLS_NOT_FOUND_DETAIL = "User with id {} does not have the required skills for this event";
    private static final String ERROR_SKILLS_NOT_FOUND = "User does not have the required skills for this event";

    private static final String EVENT_ID_MISMATCH_DETAIL = "ID in request path {} does not match ID in request body {}";
    private static final String EVENT_ID_MISMATCH = "ID in request path does not match ID in request body";

    public EventDto create(EventDto eventDto) {
        User owner = validateOwnerAndSkills(eventDto);
        Event entity = eventMapper.toEntity(eventDto, skillRepository);
        entity.setOwner(owner);
        entity = eventRepository.save(entity);

        return eventMapper.toDto(entity);
    }

    public EventDto getEvent(long eventId) {
        return eventRepository.findById(eventId)
                .map(eventMapper::toDto)
                .orElseThrow(() -> {
                    log.error(EVENT_NOT_FOUND_LOG_DETAIL, eventId);
                    return new DataValidationException(String.format(EVENT_NOT_FOUND_LOG));
                });
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filterDto) {
        List<EventFilter> filters = new ArrayList<>();
        filters.add(new TitleFilter(filterDto.getTitle()));
        filters.add(new DateRangeFilter(filterDto.getStartDate(), filterDto.getEndDate()));
        filters.add(new OwnerFilter(filterDto.getOwnerId()));

        return eventRepository.findAll().stream()
                .filter(event -> filters.stream()
                        .allMatch(filter -> filter.matches(event)))
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
    }

    public void deleteEvent(long eventId) {
        validateEventID(eventId);
        eventRepository.deleteById(eventId);
    }

    public EventDto updateEvent(EventDto eventDto, long eventId) {
        validateEventID(eventId);
        validateMismatched(eventId);
        validateOwnerAndSkills(eventDto);

        Event existingEvent = eventRepository.getReferenceById(eventId);

        eventMapper.updateEventFormDto(eventDto, existingEvent, skillRepository);
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

    private User validateOwnerAndSkills(EventDto eventDto) {
        User owner = userRepository.findById(eventDto.getOwnerId())
                .orElseThrow(() -> {
                    log.error(ERROR_USER_NOT_FOUND_DETAIL, eventDto.getOwnerId());
                    return new DataValidationException(ERROR_USER_NOT_FOUND);
                });

        Set<Long> userSkillIds = owner.getSkills().stream()
                .map(Skill::getId)
                .collect(Collectors.toSet());

        Set<Long> requiredSkillIds = Set.copyOf(eventDto.getRelatedSkills());

        if (!userSkillIds.containsAll(requiredSkillIds)) {
            log.error(ERROR_SKILLS_NOT_FOUND_DETAIL, eventDto.getOwnerId());
            throw new DataValidationException(ERROR_SKILLS_NOT_FOUND);
        }

        return owner;
    }

    private void validateEventID(long eventId) {
        if (!eventRepository.existsById(eventId)) {
            log.error(EVENT_NOT_FOUND_LOG_DETAIL, eventId);
            throw new DataValidationException(EVENT_NOT_FOUND_LOG);
        }
    }

    private void validateMismatched(long eventId) {
        if (!eventRepository.existsById(eventId)) {
            log.error(EVENT_ID_MISMATCH_DETAIL, eventId);
            throw new DataValidationException(EVENT_ID_MISMATCH);
        }
    }
}
