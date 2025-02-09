package school.faang.user_service.service.event;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserParticipantDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.user.UserParticipantMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventParticipantServiceImplTest {

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserParticipantMapper userParticipantMapper;

    @InjectMocks
    private EventParticipantServiceImpl eventParticipantService;

    @Test
    public void testRegisterParticipant() {
        when(userRepository.findById(eq(1L))).thenReturn(
                Optional.ofNullable(getUser())
        );
        when(eventRepository.findById(eq(1L))).thenReturn(
                Optional.ofNullable(getEvent())
        );

        eventParticipantService.registerParticipant(1L, 1L);

        verify(userRepository).findById(1L);
        verify(eventRepository).findById(1L);
    }

    @Test
    public void testRegisterParticipantWhenEventNotFound() {
        when(eventRepository.findById(anyLong())).thenThrow(
                EntityNotFoundException.class
        );

        assertThrows(EntityNotFoundException.class, () ->
                eventParticipantService.registerParticipant(1L, 1L));
    }

    @Test
    public void testRegisterParticipantWhenUserNotFound() {
        when(eventRepository.findById(anyLong())).thenReturn(
                Optional.ofNullable(getEvent())
        );
        when(userRepository.findById(anyLong())).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () ->
                eventParticipantService.registerParticipant(1L, 1L));
    }

    @Test
    public void testFindAllParticipantByEventId() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.ofNullable(getEvent()));
        when(eventParticipationRepository.findAllParticipantsByEventId(eq(1L))).thenReturn(
                List.of(getUser())
        );

        List<UserParticipantDto> result = eventParticipantService.findAllParticipantByEventId(1L);

        assertEquals(getExpectedUsers(), result);
    }

    @Test
    public void testFindAllParticipantByEventIdWhenEventNotFound() {
        when(eventRepository.findById(anyLong())).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> eventParticipantService.findAllParticipantByEventId(1L));
    }

    @Test
    public void testCountParticipant() {
        when(eventRepository.findById(eq(1L))).thenReturn(Optional.ofNullable(getEvent()));
        when(eventParticipationRepository.countParticipants(eq(1L))).thenReturn(2);
        int count = eventParticipantService.countParticipant(1L);

        assertEquals(2, count);
    }

    @Test
    public void testCountParticipantWhenUserNotFound() {
        when(eventRepository.findById(anyLong())).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> eventParticipantService.countParticipant(1L));
    }

    private List<UserParticipantDto> getExpectedUsers() {
        return Stream.of(getUser())
                .map(userParticipantMapper::toDto)
                .toList();
    }

    private static User getUser() {
        return User.builder().id(1L).build();
    }

    private static Event getEvent() {
        return Event.builder().id(1L).build();
    }
}