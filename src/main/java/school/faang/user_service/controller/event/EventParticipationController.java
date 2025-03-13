package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class EventParticipationController {
    private final EventParticipationService participationService;

    public void registerParticipant(long eventId, long userId) {
        participationService.registerParticipant(eventId, userId);
    }

    public void unregisterParticipant(long eventId, long userId) {
        participationService.unregisterParticipant(eventId, userId);
    }

    public List<UserDto> getParticipant(long eventId) {
        return participationService.getParticipants(eventId);
    }

    public long getParticipantsCount(long eventId) {
        return participationService.getParticipantsCount(eventId);
    }
}
