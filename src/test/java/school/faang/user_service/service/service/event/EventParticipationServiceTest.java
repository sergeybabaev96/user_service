package school.faang.user_service.service.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventParticipationServiceTest {

    @InjectMocks
    private EventParticipationService eventParticipationService;

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @Test
    public void testRegisterParticipantWhenUserNotRegistered() {
        long eventId = 1L;
        long userId = 1L;

        when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenReturn(List.of());

        eventParticipationService.registerParticipant(eventId, userId);

        verify(eventParticipationRepository, times(1))
                .register(eventId, userId);
    }

    @Test
    public void testRegisterParticipantWhenUserAlreadyRegistered() {
        long eventId = 1L;
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        List<User> participants = List.of(user);

        when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
        .thenReturn(participants);

        assertThrows(DataValidationException.class, () -> {
            eventParticipationService.registerParticipant(eventId, userId);

            verify(eventParticipationRepository, never()).register(eventId, userId);
        });
    }

    @Test
    public void testUnregisterParticipantWhenUserRegistered() {
        long eventId = 1L;
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
        .thenReturn(List.of(user));

        eventParticipationService.unregisterParticipant(eventId, userId);

        verify(eventParticipationRepository, times(1))
                .unregister(eventId, userId);
    }

    @Test
    public void testUnregisterParticipantWhenUserNotRegistered() {
        long eventId = 1L;
        long userId = 1L;

        List<User> participants = List.of();
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
        .thenReturn(participants);

        assertThrows(DataValidationException.class, () -> {
            eventParticipationService.unregisterParticipant(eventId, userId);
            verify(eventParticipationRepository, never()).unregister(eventId, userId);
        });
    }


    @Test
    public void testGetParticipants_EmptyList() {
        long eventId = 1L;
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(List.of());

        List<UserDto> result = eventParticipationService.getParticipants(eventId);

        assertEquals(List.of(), result);
    }

}
