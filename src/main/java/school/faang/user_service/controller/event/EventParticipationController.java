package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class EventParticipationController {
    private static final long MIN_ID_VALUE = 1;

    private final EventParticipationService eventParticipationService;

    public void registerParticipant(long eventId, long userId) {
        validateParticipant(eventId, userId);
        eventParticipationService.registerParticipant(eventId, userId);
    }

    public void unregisterParticipant(long eventId, long userId) {
        validateParticipant(eventId, userId);
        eventParticipationService.unregisterParticipant(eventId, userId);
    }

    public List<User> getParticipants(long eventId) {
        validateEvent(eventId);
        return eventParticipationService.getParticipants(eventId);
    }

    public int getParticipantsCount(long eventId) {
        validateEvent(eventId);
        return eventParticipationService.getParticipantsCount(eventId);
    }

    private void validateParticipant(long eventId, long userId) {
        if (eventId < MIN_ID_VALUE || userId < MIN_ID_VALUE) {
            throw new IllegalArgumentException("Event ID and User ID must be positive numbers.");
        }
    }

    private void validateEvent(long eventId) {
        if (eventId < MIN_ID_VALUE) {
            throw new IllegalArgumentException("Event ID must be positive numbers.");
        }
    }
}
