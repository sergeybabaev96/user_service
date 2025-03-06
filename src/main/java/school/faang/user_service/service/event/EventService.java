package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.EventFilterDto;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validator.EventDtoValidator;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final EventMapper eventMapper;

    public EventDto create(EventDto eventDto) {
        User owner = EventDtoValidator.validateOwnerAndSkills(eventDto, userRepository);
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

    public void deleteEvent(long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new DataValidationException(
                        String.format("Event with id %s not found", eventId))
                );
        eventRepository.delete(event);
    }

    public EventDto updateEvent(EventDto eventDto) {
        Event existingEvent = eventRepository.findById(eventDto.getId())
                .orElseThrow(() -> new DataValidationException(
                        String.format("Event with id %s not found", eventDto.getId()))
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
