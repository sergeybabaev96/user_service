package school.faang.user_service.controller.event;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@RestController
@RequestMapping("/event/participation")
@RequiredArgsConstructor
@Tag(name = "Event participation API", description = "API для управления участниками событий")
public class EventParticipationController {

    private final EventParticipationService eventParticipationService;

    @PostMapping("/register/{eventId}")
    @Operation(summary = "Регистрация в событии",
            description = "Регистрирует пользователя с переданным идентификатором в событии, на основе идентификатора")
    public void registerParticipant(@Parameter(description = "Идентификатор события") @PathVariable long eventId,
                                    @Parameter(description = "Идентификатор пользователя") @RequestParam long userId) {
        eventParticipationService.registerParticipant(eventId, userId);
    }

    @PostMapping("/unregister/{eventId}")
    @Operation(summary = "Выход из события",
            description = "Удаляет участника с переданным идентификатором из события, на основе идентификатора")
    public void unregisterParticipant(@Parameter(description = "Идентификатор события") @PathVariable long eventId,
                                      @Parameter(description = "Идентификатор пользователя") @RequestParam long userId) {
        eventParticipationService.unregisterParticipant(eventId, userId);
    }

    @GetMapping("/get/participant/{eventId}")
    @Operation(summary = "Найти участников события",
            description = "Находит всех участников события, на основе переданного идентификатора")
    public List<UserDto> getParticipant(@Parameter(description = "Идентификатор события") @PathVariable long eventId) {
        return eventParticipationService.getParticipant(eventId);
    }

    @GetMapping("/get/count/{eventId}")
    @Operation(summary = "Найти количество участников",
            description = "Высчитывает количество участников события, на основе переданного идентификатора")
    public long getParticipantsCount(@Parameter(description = "Идентификатор события") @PathVariable long eventId) {
        return eventParticipationService.getParticipantsCount(eventId);
    }
}
