package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@RestController
@RequestMapping("/event/participation")
@RequiredArgsConstructor
public class EventParticipationController {

    private final EventParticipationService eventParticipationService;

    @PostMapping("/register")
    public void registerParticipant(long eventId, long userId) {
        eventParticipationService.registerParticipant(eventId, userId);
    }

    @PostMapping("/unregister")
    public void unregisterParticipant(long eventId, long userId) {
        eventParticipationService.unregisterParticipant(eventId, userId);
    }

    @GetMapping("/get/participant")
    public List<UserDto> getParticipant(long eventId) {
        return eventParticipationService.getParticipant(eventId);
    }

    @GetMapping("/get/count")
    public long getParticipantsCount(long eventId) {
        return eventParticipationService.getParticipantsCount(eventId);
    }
}
