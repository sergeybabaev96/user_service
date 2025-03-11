package school.faang.user_service.service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.controller.event.EventParticipationController;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventParticipationService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class EventParticipationControllerTest {

    @Mock
    private EventParticipationService eventParticipationService;

    @InjectMocks
    private EventParticipationController eventParticipationController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testInvalidRegisterParticipant() {

        long invalidEventId = -1;
        long invalidUserId = -1;

        assertThrows(DataValidationException.class,
                () -> eventParticipationController.registerParticipant(invalidEventId, invalidUserId));
    }

    @Test
    public void testValidRegisterParticipant() {

        long validEventId = 1;
        long validUserId = 1;

        eventParticipationController.registerParticipant(validEventId, validUserId);

        verify(eventParticipationService, times(1)).registerParticipant(validEventId, validUserId);
    }
}