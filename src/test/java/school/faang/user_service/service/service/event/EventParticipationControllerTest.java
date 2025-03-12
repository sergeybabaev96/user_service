package school.faang.user_service.service.service.event;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.event.EventParticipationController;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventParticipationService;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EventParticipationControllerTest {

    @Mock
    private EventParticipationService eventParticipationService;

    @InjectMocks
    private EventParticipationController eventParticipationController;

    @ParameterizedTest
    @MethodSource("provideInvalidIdsForExceptionTests")
    public void testInvalidOperations(long eventId, long userId, String operation) {
        switch (operation) {
            case "register" -> assertThrows(DataValidationException.class,
                    () -> eventParticipationController.registerParticipant(eventId, userId));
            case "unregister" -> assertThrows(DataValidationException.class,
                    () -> eventParticipationController.unregisterParticipant(eventId, userId));
            case "getParticipants" -> assertThrows(DataValidationException.class,
                    () -> eventParticipationController.getParticipants(eventId));
            case "getParticipantsCount" -> assertThrows(DataValidationException.class,
                    () -> eventParticipationController.getParticipantsCount(eventId));
        }
    }

    @ParameterizedTest
    @MethodSource("provideValidIdsForSuccessTests")
    public void testValidOperations(long eventId, long userId, String operation) {
        switch (operation) {
            case "register" -> {
                eventParticipationController.registerParticipant(eventId, userId);
                verify(eventParticipationService, times(1)).registerParticipant(eventId, userId);
            }
            case "unregister" -> {
                eventParticipationController.unregisterParticipant(eventId, userId);
                verify(eventParticipationService, times(1)).unregisterParticipant(eventId, userId);
            }
            case "getParticipants" -> {
                eventParticipationController.getParticipants(eventId);
                verify(eventParticipationService, times(1)).getParticipants(eventId);
            }
            case "getParticipantsCount" -> {
                eventParticipationController.getParticipantsCount(eventId);
                verify(eventParticipationService, times(1)).getParticipantsCount(eventId);
            }
        }
    }

    private static Stream<Arguments> provideInvalidIdsForExceptionTests() {
        return Stream.of(
                Arguments.of(-1, 1, "register"),
                Arguments.of(-1, 1, "unregister"),
                Arguments.of(-1, 1, "getParticipants"),
                Arguments.of(-1, 1, "getParticipantsCount")
        );
    }

    private static Stream<Arguments> provideValidIdsForSuccessTests() {
        return Stream.of(
                Arguments.of(1, 1, "register"),
                Arguments.of(1, 1, "unregister"),
                Arguments.of(1, 1, "getParticipants"),
                Arguments.of(1, 1, "getParticipantsCount")
        );
    }
}