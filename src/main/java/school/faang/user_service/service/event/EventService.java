package school.faang.user_service.service.event;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.event.CreateEventDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.UpdateEventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validator.EventValidator;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventValidator eventValidator;
    private final EventMapper eventMapper;
    private final List<EventFilter> eventFilters;

    @Transactional
    public EventDto createEvent(CreateEventDto createEventDto) {
        Event event = eventMapper.fromCreateDtoToEntity(createEventDto);
        eventValidator.validateEventInfo(event);
        eventValidator.validateCreatorSkills(createEventDto);
        Event savedEvent = eventRepository.save(event);
        return eventMapper.toDto(savedEvent);
    }

    @Transactional(readOnly = true)
    public EventDto getEvent(long id) {
        return eventMapper.toDto(eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event with such id not found!" + id)));
    }

    @Transactional(readOnly = true)
    public List<EventDto> getEventsByFilter(EventFilterDto eventFilterDto) {
        Stream<Event> eventStream = eventRepository.findAll().stream();
        for (EventFilter filter : eventFilters) {
            if (filter.isApplicable(eventFilterDto)) {
                eventStream = filter.apply(eventStream, eventFilterDto);
            }
        }
        return eventStream
                .map(eventMapper::toDto)
                .toList();
    }

    @Transactional
    public void deleteEvent(long id) {
        eventRepository.deleteById(id);
    }

    @Transactional
    public EventDto updateEvent(UpdateEventDto updateEventDto) {
        eventValidator.validateEventInfo(eventMapper.fromUpdateDtoToEntity(updateEventDto));
        Event event = eventRepository.findById(updateEventDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Event not found by id: " + updateEventDto.getId()));
        eventMapper.update(event, updateEventDto);
        event = eventRepository.save(event);
        return eventMapper.toDto(event);
    }

    @Transactional(readOnly = true)
    public List<EventDto> getOwnedEvents(long userId) {
        return eventRepository.findAllByUserId(userId)
                .stream()
                .map(eventMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EventDto> getParticipatedEvents(long userId) {
        return eventRepository.findParticipatedEventsByUserId(userId)
                .stream()
                .map(eventMapper::toDto)
                .toList();
    }

    @Transactional
    public void cancelEventsByUser(long userId) {
        List<Event> events = eventRepository.findAllByUserId(userId);

        events.forEach(event -> {
            if (event.getStatus() == EventStatus.PLANNED) {
                event.setStatus(EventStatus.CANCELED);
                eventRepository.save(event);
            }
        });

    }
}

