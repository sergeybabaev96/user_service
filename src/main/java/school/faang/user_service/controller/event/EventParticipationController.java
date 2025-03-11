package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;

    public void registerParticipant(long eventId, long userId) {
        validateId(eventId);
        validateId(userId);
        eventParticipationService.registerParticipant(eventId, userId);
    }

    public void unregisterParticipant(long eventId, long userId) {
        validateId(eventId);
        validateId(userId);
        eventParticipationService.unregisterParticipant(eventId, userId);
    }

    public List<UserDto> getParticipants(long eventId) {
        validateId(eventId);
        return eventParticipationService.getParticipants(eventId);
    }

    public int getParticipantsCount(long eventId) {
        validateId(eventId);
        return eventParticipationService.getParticipantsCount(eventId);
    }

    private static void validateId(long id) {
        if (id <= 0)
            throw new DataValidationException("Id is less than or equal to zero");
    }


}