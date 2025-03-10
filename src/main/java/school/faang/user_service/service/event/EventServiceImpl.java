package school.faang.user_service.service.event;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventRequestDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final List<EventFilter> filters;

    @Override
    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        Stream<Event> events = eventRepository.findAll().stream();

        return filters.stream()
                .filter(eventFilter -> eventFilter.isApplicable(filter))
                .reduce(events, (subtotal, eventFilter) -> eventFilter.apply(filter, subtotal),
                        (s1, s2) -> s1)
                .map(eventMapper::toEventDto)
                .toList();
    }

    @Override
    public EventDto getEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event with id " + id + " was not found"));

        return eventMapper.toEventDto(event);
    }

    @Override
    public EventDto createEvent(EventRequestDto eventDto) {
        Event event = eventMapper.toEventEntity(eventDto);

        long ownerId = eventDto.ownerId();
        event.setOwner(userRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + ownerId + " was not found")));

        List<Skill> relatedSkills = getAndValidateRelatedSkills(eventDto.relatedSkillsIds(), event.getOwner());
        event.setRelatedSkills(relatedSkills);

        eventRepository.save(event);
        return eventMapper.toEventDto(event);
    }

    @Override
    public EventDto updateEvent(EventRequestDto eventDto, Long id) {
        long ownerId = eventDto.ownerId();
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event with id " + id + " was not found"));
        validateOwner(ownerId, event);

        eventMapper.update(eventDto, event);

        List<Skill> relatedSkills = getAndValidateRelatedSkills(eventDto.relatedSkillsIds(), event.getOwner());
        event.getRelatedSkills().clear();
        event.getRelatedSkills().addAll(relatedSkills);

        eventRepository.save(event);
        return eventMapper.toEventDto(event);
    }

    @Override
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    @Override
    public List<EventDto> getOwnedEvents(Long userId) {
        return eventRepository.findAllByUserId(userId).stream()
                .map(eventMapper::toEventDto)
                .toList();
    }

    @Override
    public List<EventDto> getParticipatedEvents(Long userId) {
        return eventRepository.findParticipatedEventsByUserId(userId).stream()
                .map(eventMapper::toEventDto)
                .toList();
    }

    @Override
    public Event findByIdOrThrow(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event is not exists! id: " + eventId));
    }

    @Transactional
    @Override
    public void banUsers(List<Long> userIdsToBan) {
        log.info("Trying to ban users: {}", userIdsToBan);
        List<User> usersToBan = getAllUsersByIds(userIdsToBan);
        usersToBan.forEach(v -> v.setBanned(true));
    }

    private List<User> getAllUsersByIds(List<Long> ids) {
        return userRepository.findAllById(ids);
    }

    private List<Skill> getAndValidateRelatedSkills(List<Long> relatedSkillsIds, User owner) {
        List<Skill> relatedSkills = relatedSkillsIds == null ? new ArrayList<>()
                : skillRepository.findAllById(relatedSkillsIds);
        List<Skill> ownerSkills = owner.getSkills();

        for (var skill : relatedSkills) {
            if (!ownerSkills.contains(skill)) {
                throw new DataValidationException("User with id " + owner.getId()
                        + " doesn't have enough skills to be the event owner");
            }
        }

        return relatedSkills;
    }

    private void validateOwner(long ownerId, Event event) {
        if (ownerId != event.getOwner().getId()) {
            throw new DataValidationException("User with id " + ownerId + " can't update event with id "
                    + event.getId() + ", because he is not this event owner");
        }
    }
}
