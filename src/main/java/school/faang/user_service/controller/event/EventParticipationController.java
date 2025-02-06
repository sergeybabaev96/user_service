package school.faang.user_service.controller.event;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;

    @PutMapping("/events/{id}/register-participant/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void registerParticipant(
            @PathVariable("id")
            @Positive
            long eventId,
            @PathVariable("userId")
            @Positive
            long userId
    ) {
        eventParticipationService.registerParticipant(eventId, userId);
    }

    @PutMapping("/events/{id}/unregister-participant/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unregisterParticipant(
            @PathVariable("id")
            @Positive
            long eventId,
            @PathVariable("userId")
            @Positive
            long userId
    ) {
        eventParticipationService.unregisterParticipant(eventId, userId);
    }

    @GetMapping("/events/{id}/participants")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getParticipants(
            @PathVariable("id")
            @Positive
            long eventId
    ) {
        return eventParticipationService.getParticipants(eventId);
    }

    @GetMapping("/events/{id}/participants-count")
    @ResponseStatus(HttpStatus.OK)
    public int getParticipantsCount(
            @PathVariable("id")
            @Positive
            long eventId
    ) {
        return eventParticipationService.getParticipantsCount(eventId);
    }

}
