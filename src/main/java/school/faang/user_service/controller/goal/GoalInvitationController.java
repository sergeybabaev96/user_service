package school.faang.user_service.controller.goal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.filter.goal.invitation.GoalInvitationFilterDto;
import school.faang.user_service.service.goal.GoalInvitationService;

import java.util.List;

@Tag(name = "Приглашение для целей")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/invitation-goals")
public class GoalInvitationController {
    private final GoalInvitationService service;

    @Operation(summary = "Отправить приглашение")
    @PostMapping
    public void createInvitation(@RequestBody GoalInvitationDto invitation) {
        service.createInvitation(invitation);
    }

    @Operation(summary = "Принять приглашение")
    @PutMapping("/accept/{id}")
    public void acceptGoalInvitation(@PathVariable("id") long id) {
        service.acceptGoalInvitation(id);
    }

    @Operation(summary = "Отклонить приглашение")
    @PutMapping("/reject/{id}")
    public void rejectGoalInvitation(@PathVariable("id") long id) {
        service.rejectGoalInvitation(id);
    }

    @Operation(summary = "Получить все приглашения, используя фильтры")
    @PostMapping("/all")
    public List<GoalInvitationDto> getInvitations(@RequestBody GoalInvitationFilterDto dto) {
        return service.getInvitationsWithFilters(dto);
    }
}
