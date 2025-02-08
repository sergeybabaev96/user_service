package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.filter.EventFilter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventFilter eventFilter;

    @Spy
    private EventMapper eventMapper = Mappers.getMapper(EventMapper.class);

    @InjectMocks
    private EventService eventService;

    private EventDto validEventDto;

    @BeforeEach
    void setUp() {
        validEventDto = new EventDto(
                1L,
                "Test Event",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                1L,
                "Test Description",
                List.of(1L),
                "Test Location",
                100,
                null,
                null
        );
    }

    @Test
    void createEvent_ValidInputShouldReturnEventDto() {
        User owner = new User();
        owner.setId(1L);
        owner.setSkills(new ArrayList<>(List.of(
                Skill.builder().id(1L).title("Skill 1").build()
        )));

        Event eventEntity = new Event();
        eventEntity.setId(1L);
        eventEntity.setTitle(validEventDto.title());
        eventEntity.setOwner(owner);

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(eventRepository.save(any(Event.class))).thenReturn(eventEntity);

        EventDto result = eventService.create(validEventDto);

        assertNotNull(result);
        assertEquals("Test Event", result.title());
        assertEquals(1L, result.id());
        verify(userRepository, times(1)).findById(1L);
        verify(eventRepository, times(1)).save(any(Event.class));
        verify(eventMapper, times(1)).toEntity(validEventDto);
        verify(eventMapper, times(1)).toDto(eventEntity);
    }

    @Test
    void getEvent_EventExistsShouldReturnEventDto() {
        long eventId = 1L;
        Event event = new Event();
        event.setId(eventId);
        event.setTitle("Event 1");

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        EventDto result = eventService.getEvent(eventId);

        assertNotNull(result);
        assertEquals(eventId, result.id());
        assertEquals("Event 1", result.title());

        verify(eventRepository, times(1)).findById(eventId);
        verify(eventMapper, times(1)).toDto(event);
    }

    @Test
    void getEvent_EventDoesNotExistShouldThrowException() {
        long eventId = 1L;
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(
                DataValidationException.class,
                () -> eventService.getEvent(eventId)
        );

        assertEquals("Event not found with ID: " + eventId, exception.getMessage());
        verify(eventRepository, times(1)).findById(eventId);
        verifyNoInteractions(eventMapper);
    }

    @Test
    void getEventsByFilter_FilterIsApplicableShouldReturnFilteredEvents() {
        Event event1 = new Event();
        event1.setId(1L);
        event1.setTitle("Event 1");

        Event event2 = new Event();
        event2.setId(2L);
        event2.setTitle("Event 2");

        List<Event> events = List.of(event1, event2);
        EventFilterDto filterDto = new EventFilterDto(null, null, null, null, null);
        when(eventRepository.findAll()).thenReturn(events);
        when(eventFilter.isApplicable(filterDto)).thenReturn(true);
        when(eventFilter.apply(any(Stream.class), eq(filterDto))).thenAnswer(invocation -> {
            Stream<Event> stream = invocation.getArgument(0);
            return stream.filter(event -> event.getId() == 1L);
        });

        List<EventFilter> filters = List.of(eventFilter);
        ReflectionTestUtils.setField(eventService, "filters", filters);

        List<EventDto> result = eventService.getEventsByFilter(filterDto);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals("Event 1", result.get(0).title());

        verify(eventRepository, times(1)).findAll();
        verify(eventFilter, times(1)).isApplicable(filterDto);
        verify(eventFilter, times(1)).apply(any(Stream.class), eq(filterDto));
        verify(eventMapper, times(1)).toDto(any(Event.class));
    }

    @Test
    void deleteEvent_EventDoesNotExistShouldThrowException() {
        long eventId = 1L;
        when(eventRepository.existsById(eventId)).thenReturn(false);

        DataValidationException exception = assertThrows(
                DataValidationException.class,
                () -> eventService.deleteEvent(eventId)
        );

        assertEquals("Event not found with ID: " + eventId, exception.getMessage());
        verify(eventRepository, times(1)).existsById(eventId);
        verify(eventRepository, never()).deleteById(eventId);
    }

    @Test
    void deleteEvent_EventExistsShouldDeleteEvent() {
        long eventId = 1L;
        when(eventRepository.existsById(eventId)).thenReturn(true);

        eventService.deleteEvent(eventId);

        verify(eventRepository, times(1)).existsById(eventId);
        verify(eventRepository, times(1)).deleteById(eventId);
    }
}
