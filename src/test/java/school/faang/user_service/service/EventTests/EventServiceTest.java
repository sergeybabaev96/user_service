package school.faang.user_service.service.EventTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDTO;
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

    @Test
    void create_shouldCreateEventWhenValid() {
        validDTO();
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));
        when(eventMapper.eventDTOToEvent(mockEventDTO)).thenReturn(mockEvent);
        when(eventMapper.eventToEventDTO(mockEvent)).thenReturn(mockEventDTO);

        EventDTO result = eventService.create(mockEventDTO);

        assertNotNull(result);
        verify(eventRepository).save(mockEvent);
    }

    @Test
    void create_shouldThrowExceptionWhenOwnerNotFound() {
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.empty());

        DataValidationException thrown = assertThrows(DataValidationException.class, () -> {
            eventService.create(mockEventDTO);
        });

        assertEquals("Owner not found", thrown.getMessage());
    }

    @Test
    void getById_shouldReturnEventDTOWhenFound() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(mockEvent));
        when(eventMapper.eventToEventDTO(mockEvent)).thenReturn(mockEventDTO);

        EventDTO result = eventService.getById(1L);

        assertEquals(mockEventDTO, result);
    }

    @Test
    void getById_shouldThrowExceptionWhenEventNotFound() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        DataValidationException thrown = assertThrows(DataValidationException.class, () -> {
            eventService.getById(1L);
        });

        assertEquals("Event not found", thrown.getMessage());
    }

    @Test
    void update_shouldUpdateEventWhenValid() {
        validDTO();
        when(eventRepository.findById(1L)).thenReturn(Optional.of(mockEvent));
        when(userRepository.findById(mockEventDTO.getOwnerId())).thenReturn(Optional.of(mockUser));
        when(eventMapper.eventToEventDTO(mockEvent)).thenReturn(mockEventDTO);
        doNothing().when(eventMapper).updateEventFromDTO(mockEventDTO, mockEvent);

        EventDTO result = eventService.update(1L, mockEventDTO);

        assertNotNull(result);
        verify(eventRepository).save(mockEvent);
    }

    @Test
    void update_shouldThrowExceptionWhenEventNotFound() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        DataValidationException thrown = assertThrows(DataValidationException.class, () -> {
            eventService.update(1L, mockEventDTO);
        });

        assertEquals("Event not found", thrown.getMessage());
    }

    @Test
    void delete_shouldDeleteEventWhenExists() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(mockEvent));

        eventService.delete(1L);

        verify(eventRepository).delete(mockEvent);
    }

    @Test
    void delete_shouldThrowExceptionWhenEventNotFound() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        DataValidationException thrown = assertThrows(DataValidationException.class, () -> {
            eventService.delete(1L);
        });

        assertEquals("Event not found", thrown.getMessage());
    }

    private void validDTO (){
        mockEventDTO.setTitle("title");
        mockEventDTO.setStartDate(LocalDateTime.now().plusHours(1));
        mockEventDTO.setRelatedSkills(List.of(101L,102L));
        List<Skill> skills = new ArrayList<>();
        Skill skill101 = new Skill();
        skill101.setId(101L);
        Skill skill102 = new Skill();
        skill102.setId(102L);
        skills.add(skill101);
        skills.add(skill102);
        mockUser.setSkills(skills);
    }
}