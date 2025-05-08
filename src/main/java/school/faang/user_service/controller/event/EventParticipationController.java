package school.faang.user_service.controller.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.event.EventParticipationService;
import java.util.List;

@RestController
@RequestMapping("/events")
public class EventParticipationController {
    @Autowired
    private EventParticipationService eventParticipationService;

    @PostMapping("/{eventId}/register/{userId}")
    public String registerParticipant(@PathVariable long eventId, @PathVariable long userId) {
        eventParticipationService.registerParticipant(eventId, userId);
        return "User successfully registered";
    }

    @DeleteMapping("/{eventId}/unregister/{userId}")
    public String unregisterParticipant(@PathVariable long eventId, @PathVariable long userId) {
        eventParticipationService.unregisterParticipant(eventId, userId);
        return "User successfully unregistered";
    }

    @GetMapping("/{eventId}/participants")
    public List<UserDto> getParticipants(@PathVariable long eventId) {
        return eventParticipationService.getParticipants(eventId);
    }

    @GetMapping("/{eventId}/participants/count")
    public int getParticipantsCount(@PathVariable long eventId) {
        return eventParticipationService.getParticipantsCount(eventId);
    }
}
