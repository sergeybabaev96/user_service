package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final EventMapper eventMapper;
    private final List<EventFilter> eventFilters;

    public EventDto create(EventDto eventDto) {
        validateUserSkills(eventDto);
        Event event = eventMapper.toEntity(eventDto);
        User user = getUserById(eventDto.getOwnerId());
        event.setOwner(user);
        List<Skill> relatedSkills = mapSkillIdsToEntities(eventDto.getRelatedSkillsId());
        event.setRelatedSkills(relatedSkills);
        Event savedEvent = eventRepository.save(event);
        return eventMapper.toDto(savedEvent);
    }

    public EventDto getEvent(long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new DataValidationException("ID is not found" + eventId));
        return eventMapper.toDto(event);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto eventFilterDto) {
        Stream<Event> allEvents = eventRepository.findAll().stream();

        for (EventFilter eventFilter : eventFilters) {
            if (eventFilter.isApplicable(eventFilterDto)) {
                allEvents = eventFilter.apply(allEvents, eventFilterDto);
            }
        }

        return allEvents
                .map(eventMapper::toDto)
                .toList();
    }

    public void deleteEvent(long eventId) {
        eventRepository.existById(eventId)
                .orElseThrow(() -> new DataValidationException("ID is not found" + eventId));
        eventRepository.deleteById(eventId);
    }

    public EventDto updateEvent(EventDto eventDto) {
        Event event = eventMapper.toEntity(eventDto);
        validation(event);
        eventRepository.save(event);
        return eventMapper.toDto(event);
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

    private List<Skill> mapSkillIdsToEntities(List<Long> skillIds) {
        return skillIds.stream()
                .map(id -> skillRepository.findById(id)
                        .orElseThrow(() -> new DataValidationException("Skill not found for id: " + id)))
                .toList();
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException("User not found."));
    }

    private void validation(Event event) {
        User owner = event.getOwner();
        List<Skill> skillsOwner = owner.getSkills();
        for (Skill skill : skillsOwner) {
            if (!skill.getEvents().contains(event)) {
                throw new DataValidationException("User can not carry out this event - " + event);
            }
        }
    }

    private void validateUserSkills(EventDto eventDto) {
        List<Long> requiredSkillIds = eventDto.getRelatedSkillsId();

        if (requiredSkillIds == null || requiredSkillIds.isEmpty()) {
            return;
        }

        User user = userRepository.findById(eventDto.getOwnerId())
                .orElseThrow(() -> new DataValidationException("User not found."));

        List<Skill> requiredSkills = skillRepository.findAllById(requiredSkillIds);

        if (!user.getSkills().containsAll(requiredSkills)) {
            throw new DataValidationException("User does not possess required skills.");
        }
    }
}