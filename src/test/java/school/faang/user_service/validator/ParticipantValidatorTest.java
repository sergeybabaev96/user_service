package school.faang.user_service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParticipantValidatorTest {

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @InjectMocks
    private ParticipantValidator participantValidator;

    private static final long EVENT_ID = 1L;
    private static final long USER_ID = 1L;

    @Test
    void testCheckParticipantAlreadyRegistered_WhenParticipantRegistered_ThrowsException() {
        User user = User.builder().id(USER_ID).build();

        when(eventParticipationRepository.findAllParticipantsByEventId(EVENT_ID)).thenReturn(List.of(user));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> participantValidator.checkParticipantAlreadyRegistered(EVENT_ID, USER_ID));

        assertEquals("Пользователь уже зарегистрирован!", exception.getMessage());
        verify(eventParticipationRepository, times(1)).findAllParticipantsByEventId(EVENT_ID);
    }

    @Test
    void testCheckParticipantAlreadyRegistered_WhenParticipantNotRegistered_DoesNotThrowException() {
        when(eventParticipationRepository.findAllParticipantsByEventId(EVENT_ID)).thenReturn(List.of());

        assertDoesNotThrow(() -> participantValidator.checkParticipantAlreadyRegistered(EVENT_ID, USER_ID));
        verify(eventParticipationRepository, times(1)).findAllParticipantsByEventId(EVENT_ID);
    }

    @Test
    void testCheckParticipantNotRegistered_WhenParticipantNotRegistered_ThrowsException() {
        when(eventParticipationRepository.findAllParticipantsByEventId(EVENT_ID)).thenReturn(List.of());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> participantValidator.checkParticipantNotRegistered(EVENT_ID, USER_ID));

        assertEquals("Пользователь не зарегистрирован на событие!", exception.getMessage());
        verify(eventParticipationRepository, times(1)).findAllParticipantsByEventId(EVENT_ID);
    }

    @Test
    void testCheckParticipantNotRegistered_WhenParticipantRegistered_DoesNotThrowException() {
        User user = User.builder().id(USER_ID).build();
        when(eventParticipationRepository.findAllParticipantsByEventId(EVENT_ID)).thenReturn(List.of(user));

        assertDoesNotThrow(() -> participantValidator.checkParticipantNotRegistered(EVENT_ID, USER_ID));
        verify(eventParticipationRepository, times(1)).findAllParticipantsByEventId(EVENT_ID);
    }
}