package school.faang.user_service.service.EventTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDTO;
import school.faang.user_service.dto.event.EventFilterDTO;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private EventService eventService;

    private User mockUser;
    private EventDTO mockEventDTO;
    private Event mockEvent;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockEventDTO = new EventDTO();
        mockEventDTO.setId(1L);
        mockEventDTO.setOwnerId(mockUser.getId());
        mockEvent = new Event();
        mockEvent.setId(1L);
        mockEvent.setOwner(mockUser);

    }

    private void validDTO() {
        mockEventDTO.setTitle("title");
        mockEventDTO.setStartDate(LocalDateTime.now().plusHours(1));
        mockEventDTO.setRelatedSkills(List.of(101L, 102L));
        List<Skill> skills = new ArrayList<>();
        Skill skill101 = new Skill();
        skill101.setId(101L);
        Skill skill102 = new Skill();
        skill102.setId(102L);
        skills.add(skill101);
        skills.add(skill102);
        mockUser.setSkills(skills);
    }

    @Test
    void positiveCreate_shouldCreateEventWhenValid() {
        validDTO();
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));
        when(eventMapper.eventDTOToEvent(mockEventDTO)).thenReturn(mockEvent);
        when(eventMapper.eventToEventDTO(mockEvent)).thenReturn(mockEventDTO);
        EventDTO result = eventService.create(mockEventDTO);
        verify(eventRepository).save(mockEvent);
        assertNotNull(result);
    }

    @Test
    void positiveGetById_shouldReturnEventDTOWhenFound() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(mockEvent));
        when(eventMapper.eventToEventDTO(mockEvent)).thenReturn(mockEventDTO);
        EventDTO result = eventService.getById(1L);
        assertEquals(mockEventDTO, result);
    }

    @Test
    void positiveUpdate_shouldUpdateEventWhenValid() {
        validDTO();
        when(eventRepository.findById(1L)).thenReturn(Optional.of(mockEvent));
        when(userRepository.findById(mockEventDTO.getOwnerId())).thenReturn(Optional.of(mockUser));
        when(eventMapper.eventToEventDTO(mockEvent)).thenReturn(mockEventDTO);
        doNothing().when(eventMapper).updateEventFromDTO(mockEventDTO, mockEvent);
        EventDTO result = eventService.update(1L, mockEventDTO);
        verify(eventRepository).save(mockEvent);
        assertNotNull(result);
    }

    @Test
    void positiveDelete_shouldDeleteEventWhenExists() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(mockEvent));
        eventService.delete(1L);
        verify(eventRepository).delete(mockEvent);
    }

    @Test
    void positiveGetOwnedEvents_shouldGetOwnedEvents() {
        List<Event> events = List.of(new Event());
        List<EventDTO> eventDTOs = List.of(new EventDTO());

        when(eventRepository.findAllByUserId(mockUser.getId())).thenReturn(events);
        when(eventMapper.eventsToEventDTOs(events)).thenReturn(eventDTOs);

        List<EventDTO> result = eventService.getOwnedEvents(mockUser.getId());
        verify(eventRepository, times(1)).findAllByUserId(mockUser.getId());
        verify(eventMapper, times(1)).eventsToEventDTOs(events);
        assertEquals(eventDTOs, result);
    }

    @Test
    void positiveGetParticipatedEvents_shouldGetParticipatedEvents() {
        List<Event> events = List.of(new Event());
        List<EventDTO> eventDTOs = List.of(new EventDTO());
        when(eventRepository.findParticipatedEventsByUserId(mockUser.getId())).thenReturn(events);
        when(eventMapper.eventsToEventDTOs(events)).thenReturn(eventDTOs);
        List<EventDTO> result = eventService.getParticipatedEvents(mockUser.getId());
        verify(eventRepository, times(1)).findParticipatedEventsByUserId(mockUser.getId());
        verify(eventMapper, times(1)).eventsToEventDTOs(events);
        assertEquals(eventDTOs, result);
    }

    @Test
    void positiveTestGetEventsByFilter_shouldGetEventsByFilter() {
        EventFilterDTO filter = new EventFilterDTO();
        filter.setLocation("New York");

        Event event1 = new Event();
        EventDTO eventDTO1 = new EventDTO();
        eventDTO1.setLocation("New York");

        Event event2 = new Event();
        EventDTO eventDTO2 = new EventDTO();
        eventDTO2.setLocation("Los Angeles");

        List<Event> events = List.of(event1, event2);
        List<EventDTO> eventDTOs = List.of(eventDTO1, eventDTO2);

        when(eventRepository.findAll()).thenReturn(events);
        when(eventMapper.eventsToEventDTOs(events)).thenReturn(eventDTOs);

        List<EventDTO> result = eventService.getEventsByFilter(filter);
        verify(eventRepository, times(1)).findAll();
        verify(eventMapper, times(1)).eventsToEventDTOs(events);
        assertEquals(1, result.size());
        assertEquals("New York", result.get(0).getLocation());

    }

    @Test
    void negativeCreate_shouldThrowExceptionWhenOwnerNotFound() {
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.empty());
        DataValidationException thrown = assertThrows(DataValidationException.class,
                () -> eventService.create(mockEventDTO));
        assertEquals("Owner not found", thrown.getMessage());
    }

    @Test
    void negativeGetById_shouldThrowExceptionWhenEventNotFound() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());
        DataValidationException thrown = assertThrows(DataValidationException.class,
                () -> eventService.getById(1L));
        assertEquals("Event not found", thrown.getMessage());
    }

    @Test
    void negativeUpdate_shouldThrowExceptionWhenEventNotFound() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());
        DataValidationException thrown = assertThrows(DataValidationException.class,
                () -> eventService.update(1L, mockEventDTO));
        assertEquals("Event not found", thrown.getMessage());
    }

    @Test
    void negativeDelete_shouldThrowExceptionWhenEventNotFound() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());
        DataValidationException thrown = assertThrows(DataValidationException.class,
                () -> eventService.delete(1L));
        assertEquals("Event not found", thrown.getMessage());
    }

    @Test
    void negativeGetOwnedEvents_shouldThrowExceptionWhenOwnerDontHaveEvents() {
        when(eventRepository.findAllByUserId(mockUser.getId())).thenReturn(Collections.emptyList());
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> eventService.getOwnedEvents(mockUser.getId()));
        assertEquals("Owner haven`t events", exception.getMessage());
    }

    @Test
    void negativeGetParticipatedEvents_shouldThrowExceptionWhenUserDontHavePartEvents() {
        when(eventRepository.findParticipatedEventsByUserId(mockUser.getId())).thenReturn(Collections.emptyList());
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> eventService.getParticipatedEvents(mockUser.getId()));
        assertEquals("Haven`t participated events", exception.getMessage());
    }

    @Test
    void negativeTestGetEventsByFilter_shouldThrowExceptionWhenFilterIsNull() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> eventService.getEventsByFilter(null));
        assertEquals("Filter is empty", exception.getMessage());
    }

    @Test
    void negativeTestGetEventsByFilter_shouldThrowExceptionWhenEventsIsEmpty() {
        when(eventRepository.findAll()).thenReturn(Collections.emptyList());
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> eventService.getEventsByFilter(new EventFilterDTO()));
        assertEquals("Nothing to show", exception.getMessage());
    }

}