package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventParticipationService;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;

    @PutMapping("/{eventId}/users/{userId}/register")
    public void registerParticipant(@PathVariable long eventId,
                                    @PathVariable long userId) {
        validateId(eventId);
        validateId(userId);
        log.info("New request to register user with id: {} for event with id: {}", userId, eventId);
        eventParticipationService.registerParticipant(eventId, userId);
        log.info("User with id: {} was registered for event with id: {}", userId, eventId);

    }

    @DeleteMapping("/{eventId}/users/{userId}/unregister")
    public void unregisterParticipant(@PathVariable long eventId,
                                      @PathVariable long userId) {
        validateId(eventId);
        validateId(userId);
        log.info("New request to unregister user with id: {} from event with id: {}", userId, eventId);
        eventParticipationService.unregisterParticipant(eventId, userId);
        log.info("User with id: {} was unregistered from event with id: {}", userId, eventId);
    }

    @GetMapping("/{eventId}/participants")
    public List<UserDto> getParticipants(@PathVariable long eventId) {
        validateId(eventId);
        log.info("Get participants of event with id: {}", eventId);
        return eventParticipationService.getParticipants(eventId);
    }

    @GetMapping("/{eventId}/count_participants")
    public int getParticipantsCount(@PathVariable long eventId) {
        validateId(eventId);
        log.info("Get number participants of event with id: {}", eventId);
        return eventParticipationService.getParticipantsCount(eventId);
    }

    private static void validateId(long id) {
        if (id < 0) {
            throw new DataValidationException("Id is less than zero");
        }
    }

    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<String> handleDataValidationException(DataValidationException ex) {
        log.error("Data validation error: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}