package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventSkill eventSkill;
    private final EventOwner eventOwner;
    private final List<EventFilter> eventFilters;

    public EventDto create(EventDto eventDto) {
        eventSkill.checkSkillsToUser(eventDto);
        Event event = eventMapper.toEventDto(eventDto);
        event.setOwner(eventOwner.getOwner(eventDto.getOwnerId()));
        event.setRelatedSkills(eventSkill.getSkills(eventDto.getRelatedSkills()));
        Event savedEvent = eventRepository.save(event);

        return eventMapper.toEntity(savedEvent);
    }

    public EventDto getEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new DataValidationException("Event with id = %d does not exist".formatted(eventId)));
        return eventMapper.toEntity(event);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto eventFilterDto) {
        Stream<Event> allEvents = eventRepository.findAll().stream();
        for (EventFilter eventFilter : eventFilters) {
            if (eventFilter.isApplicable(eventFilterDto)) {
                allEvents = eventFilter.apply(eventFilterDto, allEvents);
            }
        }
        return allEvents.map(eventMapper::toEntity).toList();
    }

    public void deleteEvent(Long eventId) {
        eventRepository.deleteById(eventId);
    }

    public EventDto updateEvent(EventDto eventDto) {
        eventSkill.checkSkillsToUser(eventDto);
        Event updatedEvent = eventRepository.save(eventMapper.toEventDto(eventDto));
        return eventMapper.toEntity(updatedEvent);
    }

    public List<EventDto> getParticipatedEvents(Long userId) {
        List<Event> participatedEvents = eventRepository.findParticipatedEventsByUserId(userId);
        return participatedEvents.stream().map(eventMapper::toEntity).toList();
    }

    public EventDto getEvent(Long eventId) {
        Event event = eventRepository.findById(eventId).orElse(null);
        if (event == null) {
            throw new DataValidationException("The event does not exist");
        }
        return eventMapper.eventToEventDto(event);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto eventFilterDto) {
        Stream<Event> allEvents = eventRepository.findAll().stream();
        for (EventFilter eventFilter : eventFilters) {
            if (eventFilter.isApplicable(eventFilterDto)) {
                allEvents = eventFilter.apply(eventFilterDto, allEvents);
            }
        }
        return allEvents.map(eventMapper::eventToEventDto).toList();
    }

    public void deleteEvent(Long eventId) {
        eventRepository.deleteById(eventId);
    }
}
