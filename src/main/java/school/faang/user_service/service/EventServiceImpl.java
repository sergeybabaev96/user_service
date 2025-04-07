package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventSkillImpl eventSkill;
    private final EventOwnerImpl eventOwner;
    private final List<EventFilter> eventFilters;


    @Qualifier("threadPoolOfScheduling")
    private final Executor executor;

    @Value("${events.cleanup.batch}")
    private int cleanupBatch;

    @Override
    public EventDto create(EventDto eventDto) {
        eventSkill.checkSkillsToUser(eventDto);
        Event event = eventMapper.toEntity(eventDto);
        event.setOwner(eventOwner.getOwner(eventDto.ownerId()));
        event.setRelatedSkills(eventSkill.getSkills(eventDto.relatedSkills()));
        Event savedEvent = eventRepository.save(event);

        return eventMapper.toDto(savedEvent);
    }

    @Override
    public EventDto getEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new DataValidationException("Event with id = %d does not exist".formatted(eventId)));
        return eventMapper.toDto(event);
    }

    @Override
    public List<EventDto> getEventsByFilter(EventFilterDto eventFilterDto) {
        Stream<Event> allEvents = eventRepository.findAll().stream();
        for (EventFilter eventFilter : eventFilters) {
            if (eventFilter.isApplicable(eventFilterDto)) {
                allEvents = eventFilter.apply(eventFilterDto, allEvents);
            }
        }
        return allEvents.map(eventMapper::toDto).toList();
    }

    @Override
    public void deleteEvent(Long eventId) {
        eventRepository.deleteById(eventId);
    }

    @Override
    public EventDto updateEvent(EventDto eventDto) {
        eventSkill.checkSkillsToUser(eventDto);
        Event updatedEvent = eventRepository.save(eventMapper.toEntity(eventDto));
        return eventMapper.toDto(updatedEvent);
    }

    @Override
    public List<EventDto> getParticipatedEvents(Long userId) {
        List<Event> participatedEvents = eventRepository.findParticipatedEventsByUserId(userId);
        return participatedEvents.stream().map(eventMapper::toDto).toList();
    }

    @Override
    public void deleteEventByUserId(Long userId) {
        List<Event> eventToUser = eventRepository.findAllByUserId(userId);
        eventRepository.deleteAll(eventToUser);
    }

    @Override
    public void deleteParticipationFromEvent(Long userId) {
        List<Event> eventsWhereUserParticipation = eventRepository
                .findParticipatedEventsByUserId(userId);
        for (Event event : eventsWhereUserParticipation) {
            List<User> participationWithoutDeactivatedUser = event.getAttendees().stream()
                    .filter(user-> !Objects.equals(user.getId(), userId)).toList();
            event.setAttendees(participationWithoutDeactivatedUser);
        }
        eventRepository.saveAll(eventsWhereUserParticipation);
    }

    @Override
    public void cleanPastEvents() {
        int pageSize = cleanupBatch;
        int currentPage = 0;
        Page<Event> eventPage;

    }
}
