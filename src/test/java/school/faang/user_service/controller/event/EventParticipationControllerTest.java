package school.faang.user_service.controller.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.service.event.EventParticipationService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventParticipationControllerTest {
    private static final long EVENT_ID = 1L;
    private static final long USER_ID = 1L;

    @Mock
    private EventParticipationService participationService;

    @InjectMocks
    private EventParticipationController participationController;

    @Test
    void testRegisterParticipant() {
        participationController.registerParticipant(EVENT_ID, USER_ID);
        verify(participationService, times(1)).registerParticipant(EVENT_ID, USER_ID);
    }

    @Test
    void testUnregisterParticipant() {
        participationController.unregisterParticipant(EVENT_ID, USER_ID);
        verify(participationService, times(1)).unregisterParticipant(EVENT_ID, USER_ID);
    }

    @Test
    void testGetParticipant() {
        participationController.getParticipant(EVENT_ID);
        verify(participationService, times(1)).getParticipants(EVENT_ID);
    }

    @Test
    void getParticipantsCount() {
        participationController.getParticipantsCount(EVENT_ID);
        verify(participationService, times(1)).getParticipantsCount(EVENT_ID);
    }
}