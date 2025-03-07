package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.event.EventParticipationService;
import school.faang.user_service.validator.IdValidator;

@RestController
@RequiredArgsConstructor
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;
    private final IdValidator idValidator;

    public void registerParticipant(long eventId, long userId) {
        idValidator.validateId(eventId);
        idValidator.validateId(userId);
        eventParticipationService.registerParticipant(eventId, userId);
    }

    public void unregisterParticipant(long eventId, long userId) {
        idValidator.validateId(eventId);
        idValidator.validateId(userId);
        eventParticipationService.unregisterParticipant(eventId, userId);
    }

    public void getParticipants(long eventId) {
        idValidator.validateId(eventId);
        eventParticipationService.getParticipants(eventId);
    }

    public void getParticipantsCount(long eventId) {
        idValidator.validateId(eventId);
        eventParticipationService.getParticipantsCount(eventId);
    }
}
