package school.faang.user_service.service.event;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventFiltersDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.EventFilter;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.service.UserService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class EventService {
    private final UserService userService;
    private final SkillService skillService;
    private final EventRepository eventRepository;
    private final List<EventFilter> eventFilters;

    @Transactional
    public Event create(Event inputEvent, Long ownerId, List<Long> relatedSkillIds) {
        Event event = fetchOwnerAndSkills(inputEvent, ownerId, relatedSkillIds);
        validateOwnerSkills(event);
        return eventRepository.save(event);
    }

    @Transactional
    public Event getEvent(Long id) {
        log.info("Getting Event id {}", id);
        return eventRepository.findByIdOrThrow(id);
    }

    @Transactional
    public List<Event> getEventsByFilter(EventFiltersDto filters) {
        List<Event> allEvents = eventRepository.findAll();

        return eventFilters.stream()
                .filter(eventFilter -> eventFilter.isApplicable(filters))
                .flatMap(filter -> filter.apply(allEvents.stream(), filters))
                .collect(Collectors.toMap(
                        Event::getId,
                        event -> event,
                        (existing, duplicate) -> existing
                ))
                .values()
                .stream()
                .toList();
    }

    @Transactional
    public void deleteEvent(Long eventId) {
        log.info("Deleting Event id {}", eventId);
        eventRepository.deleteById(eventId);
    }

    @Transactional
    public void updateEvent(Event inputEvent, Long ownerId, List<Long> relatedSkillIds) {
        Event eventFromDto = fetchOwnerAndSkills(inputEvent, ownerId, relatedSkillIds);
        validateOwnerSkills(eventFromDto);
        Event existingEvent = eventRepository.findByIdOrThrow(eventFromDto.getId());

        updateEventFromDto(eventFromDto, existingEvent);
        eventRepository.save(existingEvent);
    }

    @Transactional
    public List<Event> getOwnedEvents(Long userId) {
        return eventRepository.findAllByUserId(userId);
    }

    @Transactional
    public List<Event> getParticipatedEvents(Long userId) {
        return eventRepository.findParticipatedEventsByUserId(userId);
    }

    private void validateOwnerSkills(Event event) {
        User owner = event.getOwner();
        Set<Skill> ownerSkills = new HashSet<>(
                Optional.ofNullable(owner.getSkills())
                        .orElse(new ArrayList<>()));
        List<Skill> relatedSkills = event.getRelatedSkills();
        if (!ownerSkills.containsAll(relatedSkills)) {
            throw new DataValidationException(
                    String.format("User with id %d don't have all related skills to create event id %d",
                            owner.getId(), event.getId()));
        }
    }

    private Event fetchOwnerAndSkills(Event inputEvent, Long ownerId, List<Long> relatedSkillIds) {
        User owner = userService.getUser(ownerId);
        List<Skill> skills = skillService.getSkills(relatedSkillIds);

        inputEvent.setOwner(owner);
        inputEvent.setRelatedSkills(skills);
        log.info("Created Event for Owner id {}. Was set skills with ids {}", ownerId, relatedSkillIds);
        return inputEvent;
    }

    private void updateEventFromDto(Event eventFromDto, Event existingEvent) {
        existingEvent.setTitle(eventFromDto.getTitle());
        existingEvent.setDescription(eventFromDto.getDescription());
        existingEvent.setStartDate(eventFromDto.getStartDate());
        existingEvent.setEndDate(eventFromDto.getEndDate());
        existingEvent.setLocation(eventFromDto.getLocation());
        existingEvent.setMaxAttendees(eventFromDto.getMaxAttendees());
        existingEvent.setOwner(eventFromDto.getOwner());
        existingEvent.setRelatedSkills(eventFromDto.getRelatedSkills());
        existingEvent.setType(eventFromDto.getType());
        existingEvent.setStatus(eventFromDto.getStatus());
        log.info("Event with id {} updated", existingEvent.getId());
    }
}
