package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
@Slf4j
public class EventParticipationController {

    private final EventParticipationService eventParticipationService;

    @PostMapping("/{eventId}/register/{userId}")
    public ResponseEntity<String> registerParticipant(@PathVariable Long eventId, @PathVariable Long userId) {
        validateId(userId);
        validateId(eventId);
        eventParticipationService.registerParticipant(eventId, userId);
        return ResponseEntity.ok("User registered for event");
    }

    public void unregisterParticipant(Long eventId, Long userId) {
        validateId(userId);
        validateId(eventId);
        eventParticipationService.unregisterParticipant(eventId, userId);
    }

    public List<UserDto> getParticipation(Long eventId) {
        validateId(eventId);
        return eventParticipationService.getParticipant(eventId);
    }

    public void getParticipantCount(Long eventId) {
        validateId(eventId);
        eventParticipationService.getParticipantCount(eventId);
    }

    private void validateId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id can't be empty");
        }
    }
}
