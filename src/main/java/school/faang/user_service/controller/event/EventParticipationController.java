package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
        return ResponseEntity.ok("User with id %s registered for event with id %s".formatted(userId, eventId));
    }

    @DeleteMapping("/{eventId}/unregister/{userId}")
    public ResponseEntity<String> unregisterParticipant(@PathVariable Long eventId, @PathVariable Long userId) {
        validateId(userId);
        validateId(eventId);
        eventParticipationService.unregisterParticipant(eventId, userId);
        return ResponseEntity.ok("User with id %s unregistered for event with id %s".formatted(userId, eventId));
    }

    @GetMapping("/{eventId}/participants")
    public ResponseEntity<List<UserDto>> getParticipation(@PathVariable Long eventId) {
        validateId(eventId);
        List<UserDto> participants = eventParticipationService.getParticipant(eventId);
        return ResponseEntity.ok(participants);
    }

    @GetMapping("/{eventId}/participants/count")
    public ResponseEntity<Integer> getParticipantCount(@PathVariable Long eventId) {
        validateId(eventId);
        int countParticipants = eventParticipationService.getParticipantCount(eventId);
        return ResponseEntity.ok(countParticipants);
    }

    private void validateId(Long id) {
        if (id == null) {
            log.warn("id can't be empty");
            throw new IllegalArgumentException("id can't be empty");
        }
    }
}
