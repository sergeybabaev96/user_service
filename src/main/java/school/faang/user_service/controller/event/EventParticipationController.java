package school.faang.user_service.controller.event;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.event.ParticipantsCountDto;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;

    @PostMapping("/{id}/participants/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
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

    @DeleteMapping("/{id}/participants/{userId}")
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

    @GetMapping("/{id}/participants")
    public List<UserDto> getParticipants(
            @PathVariable("id")
            @Positive
            long eventId
    ) {
        return eventParticipationService.getParticipants(eventId);
    }

    @GetMapping("/{id}/participants/count")
    public ParticipantsCountDto getParticipantsCount(
            @PathVariable("id")
            @Positive
            long eventId
    ) {
        return eventParticipationService.getParticipantsCount(eventId);
    }

}
