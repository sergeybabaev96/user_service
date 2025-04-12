package school.faang.user_service.service.EventTests;

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
    private final User user = getUserWithSkills();
    private final Event event = getValidEvent();
    private EventDTO eventDto = getValidEventDTO();


    @Test
    void positiveCreate_shouldCreateEventWhenValid() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(eventMapper.eventDTOToEvent(eventDto)).thenReturn(event);
        when(eventMapper.eventToEventDTO(event)).thenReturn(eventDto);
        EventDTO result = eventService.create(eventDto);
        verify(eventRepository).save(event);
        assertNotNull(result);
    }

    @Test
    void positiveGetById_shouldReturnEventDTOWhenFound() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventMapper.eventToEventDTO(event)).thenReturn(eventDto);
        EventDTO result = eventService.getById(1L);
        assertEquals(eventDto, result);
    }

    @Test
    void positiveUpdate_shouldUpdateEventWhenValid() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userRepository.findById(eventDto.getOwnerId())).thenReturn(Optional.of(user));
        when(eventMapper.eventToEventDTO(event)).thenReturn(eventDto);
        doNothing().when(eventMapper).updateEventFromDTO(eventDto, event);
        EventDTO result = eventService.update(1L, eventDto);
        verify(eventRepository).save(event);
        assertNotNull(result);
    }

    @Test
    void positiveDelete_shouldDeleteEventWhenExists() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        eventService.delete(1L);
        verify(eventRepository).delete(event);
    }

    @Test
    void positiveGetOwnedEvents_shouldGetOwnedEvents() {
        List<Event> events = List.of(new Event());
        List<EventDTO> eventDTOs = List.of(new EventDTO());

        when(eventRepository.findAllByUserId(user.getId())).thenReturn(events);
        when(eventMapper.eventsToEventDTOs(events)).thenReturn(eventDTOs);

        List<EventDTO> result = eventService.getOwnedEvents(user.getId());
        verify(eventRepository, times(1)).findAllByUserId(user.getId());
        verify(eventMapper, times(1)).eventsToEventDTOs(events);
        assertEquals(eventDTOs, result);
    }

    @Test
    void positiveGetParticipatedEvents_shouldGetParticipatedEvents() {
        List<Event> events = List.of(new Event());
        List<EventDTO> eventDTOs = List.of(new EventDTO());
        when(eventRepository.findParticipatedEventsByUserId(user.getId())).thenReturn(events);
        when(eventMapper.eventsToEventDTOs(events)).thenReturn(eventDTOs);
        List<EventDTO> result = eventService.getParticipatedEvents(user.getId());
        verify(eventRepository, times(1)).findParticipatedEventsByUserId(user.getId());
        verify(eventMapper, times(1)).eventsToEventDTOs(events);
        assertEquals(eventDTOs, result);
    }

    @Test
    void positiveTestGetEventsByFilter_shouldGetEventsByFilter() {
        String filterParam = "New York";
        EventFilterDTO filter = getEventFilter(filterParam);

        Event firstEvent = getValidEvent();
        EventDTO firstEventDto = getValidEventDTO();
        firstEventDto.setLocation("New York");

        Event secondEvent = getValidEvent();
        EventDTO secondEventDto = getValidEventDTO();
        secondEventDto.setLocation("Los Angeles");

        List<Event> events = List.of(firstEvent, secondEvent);
        List<EventDTO> eventDTOs = List.of(firstEventDto, secondEventDto);

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
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        DataValidationException thrown = assertThrows(DataValidationException.class,
                () -> eventService.create(eventDto));
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
                () -> eventService.update(1L, eventDto));
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
        when(eventRepository.findAllByUserId(user.getId())).thenReturn(Collections.emptyList());
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> eventService.getOwnedEvents(user.getId()));
        assertEquals("Owner haven`t events", exception.getMessage());
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
    private EventDTO getValidEventDTO() {
        eventDto = EventDTO.builder()
                .id(1L)
                .ownerId(1L)
                .title("title")
                .startDate(LocalDateTime.now().plusHours(1))
                .relatedSkills(List.of(101L, 102L))
                .build();
        return eventDto;
    }
    private EventFilterDTO getEventFilter(String locationFilter){
        EventFilterDTO filterDTO = new EventFilterDTO();
        filterDTO.setLocation(locationFilter);
        return filterDTO;
    }
    private User getUserWithSkills(){
        List<Skill> skills = getSkills();
        User user = new User();
        user.setId(1L);
        user.setSkills(skills);
        return user;
    }

    private List<Skill> getSkills(){
        List<Skill> skills = new ArrayList<>();
        Skill firstSkill = new Skill();
        firstSkill.setId(101L);
        Skill secondSkill = new Skill();
        secondSkill.setId(102L);
        skills.add(firstSkill);
        skills.add(secondSkill);
        return skills;
    }
    private Event getValidEvent(){
        Event event = new Event();
        event.setOwner(user);
        return event;
    }
}
