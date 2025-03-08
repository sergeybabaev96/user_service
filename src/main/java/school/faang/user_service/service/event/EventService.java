package school.faang.user_service.service.event;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final EventMapper eventMapper;

    public EventDto create(@NotNull(message = "Event can not be null") EventDto eventDto) {
        validateUserSkills(eventDto);
        Event event = eventMapper.toEntity(eventDto);
        event.setOwner(getUserById(eventDto.getOwnerId()));
        event.setRelatedSkills(mapSkillIdsToEntities(eventDto.getRelatedSkills()));
        Event savedEvent = eventRepository.save(event);
        return eventMapper.toDto(savedEvent);
    }

    private void validateUserSkills(@NotNull(message = "Event can not be null") EventDto eventDto) {
        List<Long> requiredSkills = eventDto.getRelatedSkills();
        if (requiredSkills != null && !requiredSkills.isEmpty()) {
            User user = userRepository.findById(eventDto.getOwnerId())
                    .orElseThrow(() -> new DataValidationException("User not found."));
            if (!user.getSkills().containsAll(requiredSkills)) {
                throw new DataValidationException("User does not possess required skills.");
            }
        }
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException("User not found."));
    }

    private List<Skill> mapSkillIdsToEntities(@NotNull(message = "Skills can not be null") List<Long> skillIds) {
        return skillIds.stream()
                .map(id -> skillRepository.findById(id)
                        .orElseThrow(() -> new DataValidationException("Skill not found for id: " + id)))
                .collect(Collectors.toList());
    }

    public EventDto getEvent(long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new DataValidationException("ID is not found" + eventId));
        return eventMapper.toDto(event);
    }

    public List<EventDto> getEventsByFilter(@NotNull(message = "Event can not be null") EventFilterDto filter) {
        List<Event> allEvents = eventRepository.findAll();
        List<Event> eventsAfterFilter = allEvents.stream()
                .filter(event -> filter.getTitle() == null || event.getTitle()
                        .contains(filter.getTitle()))
                .filter(event -> filter.getStartDate() == null || event.getStartDate()
                        .isAfter(filter.getStartDate()))
                .filter(event -> filter.getEndDate() == null || event.getEndDate()
                        .isBefore(filter.getEndDate()))
                .filter(event -> filter.getLocation() == null || event.getLocation()
                        .equalsIgnoreCase(filter.getLocation()))
                .toList();

        return eventsAfterFilter.stream()
                .map(eventMapper::toDto)
                .toList();
    }

    public void deleteEvent(long eventId) {
        eventRepository.findById(eventId)
                .orElseThrow(() -> new DataValidationException("ID is not found" + eventId));
        eventRepository.deleteById(eventId);
    }

    public EventDto updateEvent(@NotNull(message = "Event can not be null") EventDto eventDto) {
        Event event = eventMapper.toEntity(eventDto);
        validation(event);
        eventRepository.save(event);
        return eventMapper.toDto(event);
    }

    private void validation(@NotNull(message = "Event can not be null") Event event) {
        User owner = event.getOwner();
        List<Skill> skillsOwner = owner.getSkills();
        for (Skill skill : skillsOwner) {
            if (!skill.getEvents().contains(event)) {
                throw new DataValidationException("User can not carry out this event - " + event);
            }
        }
    }

    public List<EventDto> getOwnerEvent(long userId) {
        List<Event> events = eventRepository.findAllByUserId(userId);
        return events.stream()
                .map(eventMapper::toDto)
                .toList();
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        List<Event> events = eventRepository.findParticipatedEventsByUserId(userId);
        return events.stream()
                .map(eventMapper::toDto)
                .toList();
    }
}