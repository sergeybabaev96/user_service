package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;

    public EventDto create(EventDto eventDto) {
        User owner = userRepository.findById(eventDto.getOwnerId())
                .orElseThrow(() -> new DataValidationException(
                        String.format("User with id %s not found", eventDto.getOwnerId()))
                );

        Set<Long> userSkillIds = owner.getSkills().stream()
                .map(Skill::getId)
                .collect(Collectors.toSet());

        Set<Long> requiredSkillIds = Set.copyOf(eventDto.getRelatedSkills());

        if (!userSkillIds.containsAll(requiredSkillIds)) {
            throw new DataValidationException("User does not have the required skills to create this event");
        }

        Event entity = eventMapper.toEntity(eventDto);
        entity.setOwner(owner);
        entity = eventRepository.save(entity);

        return eventMapper.toDto(entity);
    }

    public EventDto getEvent(long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new DataValidationException(
                        String.format("Event with id %s not found", eventId))
                );
        return eventMapper.toDto(event);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        List<Event> events = eventRepository.findAll();

        return events.stream()
                .filter(event -> filter.getTitle() == null || event.getTitle().toLowerCase().contains(filter.getTitle().toLowerCase()))
                .filter(event -> filter.getStartDate() == null || !event.getStartDate().isBefore(filter.getStartDate()))
                .filter(event -> filter.getEndDate() == null || !event.getEndDate().isAfter(filter.getEndDate()))
                .filter(event -> filter.getLocation() == null || event.getLocation().equalsIgnoreCase(filter.getLocation()))
                .filter(event -> filter.getMaxAttendees() == null || event.getMaxAttendees() <= filter.getMaxAttendees())
                .filter(event -> filter.getOwnerId() == null || event.getOwner().getId().equals(filter.getOwnerId()))
                .filter(event -> filter.getEventType() == null || event.getType().equals(filter.getEventType()))
                .filter(event -> filter.getEventStatus() == null || event.getStatus().equals(filter.getEventStatus()))
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
    }
}
