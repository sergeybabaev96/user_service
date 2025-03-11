package school.faang.user_service.service.service.event;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import school.faang.user_service.controller.event.EventParticipationController;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventParticipationService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EventParticipationControllerTest {

    @InjectMocks
    EventParticipationController eventParticipationController;

    @Mock
    private EventParticipationService eventParticipationService;

    public static boolean isNegative(int number) {
        return number < 0;
    }

    @Test
    public void testUnregisterParticipantPositiveEventId() {

        assertThrows(DataValidationException.class, () -> {
            eventParticipationService.unregisterParticipant(isNegative(-2));
        });

    }

    @Test
    public void testUnregisterParticipantPositiveUserId() {

    }


}
