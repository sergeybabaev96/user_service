package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.event.EventParticipationService;
import school.faang.user_service.validator.IdValidator;

@RestController
@RequiredArgsConstructor
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;


    public void registerParticipant(long eventId, long userId) {
        IdValidator.validateId(eventId);
        IdValidator.validateId(userId);
        eventParticipationService.registerParticipant(eventId, userId);
    }

    public void unregisterParticipant(long eventId, long userId) {
        IdValidator.validateId(eventId);
        IdValidator.validateId(userId);
        eventParticipationService.unregisterParticipant(eventId, userId);
    }

    public void getParticipants(long eventId) {
        IdValidator.validateId(eventId);
        eventParticipationService.getParticipants(eventId);
    }

    public void getParticipantsCount(long eventId) {
        IdValidator.validateId(eventId);
        eventParticipationService.getParticipantsCount(eventId);
    }
}
