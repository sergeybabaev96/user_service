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
import school.faang.user_service.filter.EventFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final EventMapper eventMapper;
    private final List<EventFilter> eventFilters;

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

    public EventDto getEvent(long eventId) {
        log.info("Fetching event with ID: {}", eventId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.error("Event not found with ID: {}", eventId);
                    return new DataValidationException("Event not found with ID: " + eventId);
                });
        return eventMapper.toDto(event);
    }

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

    public EventDto updateEvent(EventDto eventDto) {
        log.info("Updating event with data: {}", eventDto);
        Event event = eventMapper.toEntity(eventDto);
        validation(event);
        Event updatedEvent = eventRepository.save(event);
        log.info("Event updated successfully with ID: {}", updatedEvent.getId());
        return eventMapper.toDto(updatedEvent);
    }

    public List<EventDto> getOwnerEvent(long userId) {
        log.info("Fetching events for owner with ID: {}", userId);
        List<Event> events = eventRepository.findAllByUserId(userId);
        List<EventDto> eventDtos = events.stream()
                .map(eventMapper::toDto)
                .toList();
        log.info("Found {} events for owner with ID: {}", eventDtos.size(), userId);
        return eventDtos;
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        log.info("Fetching participated events for user with ID: {}", userId);
        List<Event> events = eventRepository.findParticipatedEventsByUserId(userId);
        List<EventDto> eventDtos = events.stream()
                .map(eventMapper::toDto)
                .toList();
        log.info("Found {} participated events for user with ID: {}", eventDtos.size(), userId);
        return eventDtos;
    }

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

    private User getUserById(Long userId) {
        log.debug("Fetching user with ID: {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", userId);
                    return new DataValidationException("User not found with ID: " + userId);
                });
    }

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