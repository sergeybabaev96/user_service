package school.faang.user_service.service.event;

import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.event.EventFiltersDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.EventFilter;
import school.faang.user_service.filter.event.EventFilterOwnerName;
import school.faang.user_service.filter.event.EventStartDateFilter;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    private final EventFilterOwnerName eventFilterOwnerName = new EventFilterOwnerName();
    private final EventStartDateFilter eventStartDateFilter = new EventStartDateFilter();

    @Mock
    private UserService userService;
    @Mock
    private SkillService skillService;
    @Mock
    private EventRepository eventRepository;
    @InjectMocks
    private EventService eventService;
    private Event event;

    private List<Event> events;
    private List<Event> toFilter;
    private User owner;
    private User secondOwner;
    private List<Skill> skills;
    private List<Long> skillIds;
    private NoSuchElementException notFoundException;

    @BeforeEach
    void setUp() {
        Skill firstSkill = Skill.builder().title("Java").build();
        Skill secondSkill = Skill.builder().title("Spring").build();
        skills = List.of(firstSkill, secondSkill);
        skillIds = getSkillIdsList(skills);
        owner = User.builder()
                .id(1L)
                .username("firstUser")
                .skills(skills)
                .build();
        secondOwner = User.builder()
                .id(2L)
                .skills(skills)
                .username("secondOwner")
                .build();
        event = Event.builder()
                .id(1L)
                .owner(owner)
                .relatedSkills(skills)
                .build();
        Event secondEvent = Event.builder()
                .id(2L)
                .owner(secondOwner)
                .relatedSkills(skills)
                .startDate(LocalDateTime.of(2025, 1, 12, 14, 0))
                .build();
        toFilter = new ArrayList<>(Arrays.asList(event, secondEvent));
        notFoundException = new NoSuchElementException("Event id 1 not found");
        ReflectionTestUtils.setField(eventService, "batchSize", 3);
    }

    @Test
    void testGetEventsByFilterOwnerName() {
        String ownerName = secondOwner.getUsername();
        EventFiltersDto filters = EventFiltersDto.builder().ownerName(ownerName).build();

        List<Event> filteredEvents = setupAndFilterEvents(filters, eventFilterOwnerName);

        assertNotNull(filteredEvents);
        assertEquals(1, filteredEvents.size());
        assertEquals(ownerName, filteredEvents.get(0).getOwner().getUsername());
    }

    @Test
    void testGetEventsByStartDateFilter() {
        Event secondEvent = toFilter.get(1);
        LocalDateTime filterDate = LocalDateTime.of(2025, 1, 12, 12, 0);
        EventFiltersDto filters = EventFiltersDto.builder()
                .startDate(filterDate)
                .build();

        List<Event> filteredEvents = setupAndFilterEvents(filters, eventStartDateFilter);

        assertNotNull(filteredEvents);
        assertEquals(1, filteredEvents.size());
        assertEquals(secondEvent.getStartDate(), filteredEvents.get(0).getStartDate());
    }

    @Test
    void testCreateEventValidSkills() {
        Long userId = owner.getId();
        when(eventRepository.save(any())).thenReturn(event);
        when(userService.getUser(any())).thenReturn(owner);
        when(skillService.getSkills(any())).thenReturn(skills);

        Event createdEvent = eventService.create(event, userId, skillIds);

        assertNotNull(createdEvent);
        assertEquals(createdEvent.getOwner().getId(), userId);
        assertEquals(createdEvent.getRelatedSkills().size(), owner.getSkills().size());
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void testCreateEventInvalidSkills() {
        Long userId = owner.getId();
        Skill testSkill = Skill.builder().title("Python").build();
        List<Skill> modifiedSkillListForEvent = new ArrayList<>(owner.getSkills());
        modifiedSkillListForEvent.add(testSkill);
        List<Long> modifiedSkillIdsForEvent = getSkillIdsList(modifiedSkillListForEvent);

        when(userService.getUser(any())).thenReturn(owner);
        when(skillService.getSkills(any())).thenReturn(modifiedSkillListForEvent);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> eventService.create(event, userId, modifiedSkillIdsForEvent));
        assertEquals(String.format(
                "User with id %d don't have all related skills to create event id %d",
                userId, event.getId()), exception.getMessage());
    }

    @Test
    void testGetEvent() {
        Long eventId = event.getId();
        when(eventRepository.findByIdOrThrow(eventId)).thenReturn(event);

        Event foundEvent = eventService.getEvent(eventId);

        assertNotNull(foundEvent);
        assertEquals(owner.getId(), foundEvent.getId());
        verify(eventRepository, times(1)).findByIdOrThrow(eventId);
    }

    @Test
    void testGetEventNotFound() {
        Long eventId = event.getId();
        when(eventRepository.findByIdOrThrow(eventId)).thenThrow(notFoundException);

        assertThrows(NoSuchElementException.class, () -> eventService.getEvent(eventId));
        verify(eventRepository, times(1)).findByIdOrThrow(eventId);
    }


    @Test
    void testDeleteEvent() {
        Long eventId = event.getId();
        doNothing().when(eventRepository).deleteById(eventId);

        eventService.deleteEvent(eventId);

        verify(eventRepository, times(1)).deleteById(eventId);
    }

    @Test
    void testUpdateEvent() {
        Long eventId = event.getId();
        Event eventToUpdate = Event.builder()
                .id(eventId)
                .owner(owner)
                .build();
        String updatedSkillsTitle = "Java";
        List<Skill> relatedSkills = Collections.singletonList(Skill.builder().title(updatedSkillsTitle).build());
        List<Long> eventToUpdateSkillIds = getSkillIdsList(relatedSkills);
        eventToUpdate.setRelatedSkills(relatedSkills);

        when(eventRepository.findByIdOrThrow(eventId)).thenReturn(event);
        when(userService.getUser(any())).thenReturn(owner);
        when(skillService.getSkills(any())).thenReturn(skills);

        eventService.updateEvent(eventToUpdate, eventToUpdate.getId(), eventToUpdateSkillIds);

        assertEquals(eventToUpdate.getRelatedSkills().get(0).getTitle(), updatedSkillsTitle);
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void testUpdateEventNotFound() {
        Long eventId = event.getId();
        when(eventRepository.findByIdOrThrow(eventId)).thenThrow(notFoundException);
        when(userService.getUser(any())).thenReturn(owner);
        when(skillService.getSkills(any())).thenReturn(skills);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> eventService.updateEvent(event, eventId, skillIds));

        assertEquals(String.format("Event id %d not found", eventId), exception.getMessage());
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void testGetOwnedEvents() {
        Long ownerId = owner.getId();
        when(eventRepository.findAllByUserId(ownerId)).thenReturn(Collections.emptyList());

        eventService.getOwnedEvents(ownerId);

        verify(eventRepository, times(1)).findAllByUserId(ownerId);
    }

    @Test
    void testGetParticipatedEvents() {
        Long ownerId = owner.getId();
        when(eventRepository.findParticipatedEventsByUserId(ownerId)).thenReturn(Collections.emptyList());

        eventService.getParticipatedEvents(ownerId);

        verify(eventRepository, times(1)).findParticipatedEventsByUserId(ownerId);
    }

    private List<Event> setupAndFilterEvents(EventFiltersDto filters, EventFilter filter) {
        when(eventRepository.findAll()).thenReturn(toFilter);
        List<EventFilter> mockEventFilters = List.of(filter);
        eventService = new EventService(userService, skillService, eventRepository, mockEventFilters);

        return eventService.getEventsByFilter(filters);
    }

    private List<Long> getSkillIdsList(List<Skill> skills) {
        return skills.stream()
                .map(Skill::getId)
                .toList();
    }

    @Test
    void deletePassedEvents_shouldPartitionAndDeleteInBatches() {

        events = getEventList();
        when(eventRepository.findAllByEndDateBefore(any(LocalDateTime.class))).thenReturn(events);

        eventService.deletePastEvents();

        verify(eventRepository, times(3)).deleteAll(anyList());
    }

    private static @NotNull List<Event> getEventList() {
        return List.of(
                Event.builder().id(1L).endDate(LocalDateTime.now().minusDays(1)).build(),
                Event.builder().id(2L).endDate(LocalDateTime.now().minusDays(2)).build(),
                Event.builder().id(5L).endDate(LocalDateTime.now().minusDays(3)).build(),
                Event.builder().id(6L).endDate(LocalDateTime.now().minusDays(3)).build(),
                Event.builder().id(7L).endDate(LocalDateTime.now().minusDays(3)).build(),
                Event.builder().id(8L).endDate(LocalDateTime.now().minusDays(3)).build(),
                Event.builder().id(9L).endDate(LocalDateTime.now().minusDays(3)).build(),
                Event.builder().id(10L).endDate(LocalDateTime.now().plusDays(3)).build(),
                Event.builder().id(12L).endDate(LocalDateTime.now().minusDays(3)).build()
        );
    }
    @Test
    void testDeletePastEvents_NoPastEvents() {

        when(eventRepository.findAllByEndDateBefore(any(LocalDateTime.class))).thenReturn(List.of());

        eventService.deletePastEvents();

        verify(eventRepository, never()).deleteAll(anyList());
    }
}
