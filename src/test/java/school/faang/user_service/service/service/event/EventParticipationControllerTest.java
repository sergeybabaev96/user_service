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

public class EventParticipationControllerTest {

    @Mock
    private EventParticipationService eventParticipationService;

    @InjectMocks
    EventParticipationController eventParticipationController;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Инициализация моков
    }

    @Test
    public void testUnregisterParticipantPositiveEventId() {
        assertThrows(DataValidationException.class,
                () -> eventParticipationService.registerParticipant(-1, 11));
    }
//
//    @Test
//    public void testUnregisterParticipantPositiveUserId() {
//
//    }
//
}

