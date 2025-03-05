package school.faang.user_service.controller.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import school.faang.user_service.entity.User;
import school.faang.user_service.exceptions.EventParticipationException;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@Controller
@RequestMapping("/event")
public class EventParticipationController {
    private EventParticipationService participationService;

    @Autowired
    public EventParticipationController(EventParticipationService participationService) {
        this.participationService = participationService;
    }

    @PostMapping("/registration")
    public void registerParticipant(long eventId, long userId ) {
        participationService.registerParticipant(eventId, userId);
    }

    @PostMapping("/unregistration")
    public void unregisterParticipant(long eventId, long userId) {
        participationService.unregisterParticipant(eventId, userId);
    }

    @GetMapping("/{id}")
    public List<User> getParticipant(@PathVariable("id") long eventId) {
        return participationService.getParticipant(eventId);
    }

    @GetMapping("/{id}/count")
    public long getParticipantsCount(@PathVariable("id") long eventId) {
        return participationService.getParticipantsCount(eventId);
    }
}
