package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.event.EventParticipationController;
import school.faang.user_service.service.event.EventParticipationService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventParticipationControllerTest {
    @Mock
    private EventParticipationService eventParticipationService;
    private final long eventId = 1;
    private final long userId = 1;
    private final int countInvocation = 1;

    @InjectMocks
    private EventParticipationController eventParticipationController;

    @Test
    void positiveInvocationRegister() {
        eventParticipationController.registerParticipant(eventId, userId);
        verify(eventParticipationService, times(countInvocation)).registerParticipant(eventId, userId);
    }

    @Test
    void positiveInvocationUnregister() {
        eventParticipationController.unregisterParticipant(eventId, userId);
        verify(eventParticipationService, times(countInvocation)).unregisterParticipant(eventId, userId);
    }

    @Test
    void positiveInvocationGetParticipant() {
        eventParticipationController.getParticipant(eventId);
        verify(eventParticipationService, times(countInvocation)).getParticipant(eventId);
    }

    @Test
    void positiveInvocationGetParticipantsCount() {
        eventParticipationController.getParticipantsCount(eventId);
        verify(eventParticipationService, times(countInvocation)).getParticipantsCount(eventId);
    }
}
