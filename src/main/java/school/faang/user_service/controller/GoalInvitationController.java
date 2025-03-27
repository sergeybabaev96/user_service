package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.service.GoalInvitationService;

import java.util.List;


@RestController
@RequestMapping("/goal-invitations")
@AllArgsConstructor
@Slf4j
@Tag(name = "Goal invitation API", description = "API для управления приглашениями к совместному достижению целей")
public class GoalInvitationController {
    private final GoalInvitationService service;


    @PostMapping("/create")
    @Operation(summary = "Создать приглашение",
            description = "Создает приглашения присоединиться к цели, на основе переданных данных")
    public GoalInvitationDto createInvitation(@RequestBody GoalInvitationDto goalInvitationDto) {
        return service.create(goalInvitationDto);
    }

    @PostMapping("/accept/{id}")
    @Operation(summary = "Принять приглашение",
            description = "Принимает приглашение присоединиться к цели с переданным идентификатором")
    public GoalInvitationDto acceptGoalInvitation(
            @Parameter(description = "Идентификатор приглашения") @PathVariable long id) {
        return service.accept(id);
    }

    @PostMapping("/reject/{id}")
    @Operation(summary = "Отклонить приглашение",
            description = "Отклоняет приглашение присоединиться к цели с переданным идентификатором")
    public GoalInvitationDto rejectGoalInvitation(
            @Parameter(description = "Идентификатор приглашения") @PathVariable long id) {
        return service.reject(id);
    }

    @PostMapping("/filter")
    @Operation(summary = "Найти приглашения",
            description = "Находит все приглашения присоединиться к целям," +
                    " и выводит их в соответствии с переданным фильтром")
    public List<GoalInvitationDto> getInvitations(@RequestBody InvitationFilterDto filter) {
        return service.getInvitations(filter);
    }

}
