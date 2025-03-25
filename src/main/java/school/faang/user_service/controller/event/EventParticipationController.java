package school.faang.user_service.controller.event;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
//@Tag(name = "Event Participation", description = "API for managing event participations")
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;

    @Operation(
            summary = "Register user for an event",
            description = "Register a user as a participant of a specific event"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
            @ApiResponse(responseCode = "404", description = "Event or user not found")
    })
    @PutMapping("/{eventId}/users/{userId}/register")
    public void registerParticipant(
            @Parameter(description = "ID of the event", required = true) @PathVariable long eventId,
            @Parameter(description = "ID of the user to register", required = true) @PathVariable long userId) {
        validateId(eventId);
        validateId(userId);
        log.info("New request to register user with id: {} for event with id: {}", userId, eventId);
        eventParticipationService.registerParticipant(eventId, userId);
        log.info("User with id: {} was registered for event with id: {}", userId, eventId);
    }

    @Operation(
            summary = "Unregister user from an event",
            description = "Remove a user from participants of a specific event"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User unregistered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied")
    })
    @DeleteMapping("/{eventId}/users/{userId}/unregister")
    public void unregisterParticipant(
            @Parameter(description = "ID of the event", required = true) @PathVariable long eventId,
            @Parameter(description = "ID of the user to unregister", required = true) @PathVariable long userId) {
        validateId(eventId);
        validateId(userId);
        log.info("New request to unregister user with id: {} from event with id: {}", userId, eventId);
        eventParticipationService.unregisterParticipant(eventId, userId);
        log.info("User with id: {} was unregistered from event with id: {}", userId, eventId);
    }

    @Operation(
            summary = "Get event participants",
            description = "Retrieve list of all participants for a specific event"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Participants list retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDto.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied")
    })
    @GetMapping("/{eventId}/participants")
    public List<UserDto> getParticipants(
            @Parameter(description = "ID of the event", required = true) @PathVariable long eventId) {
        validateId(eventId);
        log.info("Get participants of event with id: {}", eventId);
        return eventParticipationService.getParticipants(eventId);
    }

    @Operation(
            summary = "Get participants count",
            description = "Retrieve number of participants for a specific event"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Participants count retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied")
    })
    @GetMapping("/{eventId}/count_participants")
    public int getParticipantsCount(
            @Parameter(description = "ID of the event", required = true) @PathVariable long eventId) {
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
