package school.faang.user_service.service.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.event.EventParticipationController;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventParticipationService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EventParticipationControllerTest {

    @Mock
    private EventParticipationService eventParticipationService;

    @InjectMocks
    private EventParticipationController eventParticipationController;

    //1//
    @Test
    public void testInvalidRegisterParticipant() {

        long invalidEventId = -1;
        long invalidUserId = 1;

        assertThrows(DataValidationException.class,
                () -> eventParticipationController.registerParticipant(invalidEventId, invalidUserId));
    }

    @Test
    public void testValidRegisterParticipant() {

        long validEventId = 1;
        long validUserId = 1;

        eventParticipationController.registerParticipant(validEventId, validUserId);

        verify(eventParticipationService, times(1))
                .registerParticipant(validEventId, validUserId);
    }

    @Test
    public void testRegisterParticipation() {
        eventParticipationController.registerParticipant(1, 1);

        verify(eventParticipationService, times(1))
                .registerParticipant(1, 1);

    }

    //2//
    @Test
    public void testInvalidUnregisterParticipant() {

        long invalidEventId = -1;
        long invalidUserId = 1;

        assertThrows(DataValidationException.class,
                () -> eventParticipationController.unregisterParticipant(invalidEventId, invalidUserId));
    }

    @Test
    public void testValidUnregisterParticipant() {

        long validEventId = 0;
        long validUserId = 1;

        eventParticipationController.unregisterParticipant(validEventId, validUserId);

        verify(eventParticipationService, times(1))
                .unregisterParticipant(validEventId, validUserId);
    }

    @Test
    public void testUnregisterParticipant() {
        eventParticipationController.unregisterParticipant(1, 1);

        verify(eventParticipationService, times(1))
                .unregisterParticipant(1, 1);

    }

    //3//
    @Test
    public void testInvalidGetParticipants() {
        long invalidEventId = -1;

        assertThrows(DataValidationException.class,
                () -> eventParticipationController.getParticipants(invalidEventId));
    }

    @Test
    public void testGetParticipants() {

       eventParticipationController.getParticipants(1);
       verify(eventParticipationService, times(1))
       .getParticipants(1);
    }

}