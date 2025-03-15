package school.faang.user_service.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EventNotFoundException;
import school.faang.user_service.exception.EventParticipationException;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class EventParticipationServiceImplTest {
    private static final long EVENT_ID = 1L;
    private static final long USER_ID = 1L;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventParticipationRepository participationRepository;

    @InjectMocks
    private EventParticipationServiceImpl participationService;

    @Spy
    private UserMapperImpl userMapper;

    @Test
    void testRegisterParticipantWhenEventDoesNotExist() {
        when(eventRepository.existsById(EVENT_ID)).thenReturn(false);
        assertThrows(EventNotFoundException.class, () -> participationService.registerParticipant(EVENT_ID, USER_ID));
    }

    @Test
    void testRegisterParticipantWhenUserAlreadyRegistered() {
        when(eventRepository.existsById(EVENT_ID)).thenReturn(true);
        when(participationRepository.findAllParticipantsByEventId(EVENT_ID))
                .thenReturn(List.of(User.builder().id(USER_ID).build()));
        assertThrows(EventParticipationException.class, () ->
                participationService.registerParticipant(EVENT_ID, USER_ID));
    }

    @Test
    void testRegisterParticipant() {
        when(eventRepository.existsById(EVENT_ID)).thenReturn(true);
        when(participationRepository.findAllParticipantsByEventId(EVENT_ID)).thenReturn(List.of());
        participationService.registerParticipant(EVENT_ID, USER_ID);
        verify(participationRepository, times(1)).register(EVENT_ID, USER_ID);
    }

    @Test
    void testUnregisterParticipantWhenEventDoesNotExists() {
        when(eventRepository.existsById(EVENT_ID)).thenReturn(false);
        assertThrows(EventNotFoundException.class, () -> participationService.unregisterParticipant(EVENT_ID, USER_ID));
    }

    @Test
    void testUnregisterParticipantWhenUserNotRegisteredForEvent() {
        when(eventRepository.existsById(EVENT_ID)).thenReturn(true);
        when(participationRepository.findAllParticipantsByEventId(EVENT_ID)).thenReturn(List.of());
        assertThrows(EventParticipationException.class, () ->
                participationService.unregisterParticipant(EVENT_ID, USER_ID));
    }

    @Test
    void testUnregisterParticipant() {
        when(eventRepository.existsById(EVENT_ID)).thenReturn(true);
        when(participationRepository.findAllParticipantsByEventId(EVENT_ID))
                .thenReturn(List.of(
                        User.builder()
                                .id(USER_ID)
                                .build()
                ));
        participationService.unregisterParticipant(EVENT_ID, USER_ID);
        verify(participationRepository, times(1)).unregister(EVENT_ID, USER_ID);
    }


    @Test
    void testGetParticipantsWhenEventDoesNotExists() {
        when(eventRepository.existsById(EVENT_ID)).thenReturn(false);
        assertThrows(EventNotFoundException.class, () -> participationService.getParticipants(EVENT_ID));
    }

    @Test
    void testGetParticipantsWhenEventExistsButHasNoUsers() {
        when(eventRepository.existsById(EVENT_ID)).thenReturn(true);
        List<UserDto> userDtos = List.of();
        assertEquals(userDtos, participationService.getParticipants(EVENT_ID));
    }

    @Test
    void testGetParticipantsWhenEventExistsAndHasUsers() {
        when(eventRepository.existsById(EVENT_ID)).thenReturn(true);
        when(participationRepository.findAllParticipantsByEventId(EVENT_ID))
                .thenReturn(List.of(User.builder().id(USER_ID).build()));
        List<UserDto> userDtos = List.of(new UserDto(USER_ID));
        assertEquals(userDtos, participationService.getParticipants(EVENT_ID));
    }

    @Test
    void testGetParticipantsCountWhenEventDoesNotExist() {
        when(eventRepository.existsById(EVENT_ID)).thenReturn(false);
        assertThrows(EventNotFoundException.class, () -> participationService.getParticipantsCount(EVENT_ID));
    }

    @Test
    void testGetParticipantsCountWhenEventExists() {
        when(eventRepository.existsById(EVENT_ID)).thenReturn(true);
        participationService.getParticipantsCount(EVENT_ID);
        verify(participationRepository, times(1)).countParticipants(EVENT_ID);
    }
}
