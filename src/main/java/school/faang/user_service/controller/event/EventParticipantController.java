package school.faang.user_service.controller.event;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.user.UserParticipantDto;
import school.faang.user_service.service.event.EventParticipantService;

import java.util.List;

@Tag(name = "Участники события")
@RestController
@RequiredArgsConstructor
@RequestMapping("${domain.path}/event-participants")
@Validated
public class EventParticipantController {
    private final EventParticipantService eventParticipantService;

    @Operation(summary = "Регистрация участника события")
    @PostMapping("/register/event/{eventId}/user/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerParticipant(@PathVariable @NotNull @Min(0) long eventId,
                                    @PathVariable @NotNull @Min(0) long userId) {
        eventParticipantService.registerParticipant(eventId, userId);
    }

    @Operation(summary = "Отменить регистрацию на событие")
    @PostMapping("/unregister/event/{eventId}/user/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unregisterParticipant(@PathVariable @NotNull @Min(0) long eventId,
                                      @PathVariable @NotNull @Min(0) long userId) {
        eventParticipantService.unregisterParticipant(eventId, userId);
    }

    @Operation(summary = "Получить всех участников события")
    @GetMapping("/event/{eventId}")
    public List<UserParticipantDto> findAllParticipantByEventId(@PathVariable @NotNull @Min(0) long eventId) {
        return eventParticipantService.findAllParticipantByEventId(eventId);
    }

    @Operation(summary = "Получить количество участников события")
    @GetMapping("/count/{eventId}")
    public int getCountParticipant(@PathVariable @NotNull @Min(0) long eventId) {
        return eventParticipantService.countParticipant(eventId);
    }
}
