package school.faang.user_service.controller.goal;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.GoalInvitationDtoResponse;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.service.goal.GoalInvitationService;

import java.util.List;

@RestController
@RequestMapping("/goals/invitations")
@RequiredArgsConstructor
public class GoalInvitationController {

    private final GoalInvitationService goalInvitationService;

    @PostMapping()
    public GoalInvitationDtoResponse createInvitation(@RequestBody GoalInvitationDto invitation) {
        return goalInvitationService.createInvitation(invitation);
    }

    @PatchMapping("/{id}/accept")
    public GoalInvitationDtoResponse acceptGoalInvitation(@PathVariable("id") @NotNull Long id) {
        return goalInvitationService.acceptGoalInvitation(id);
    }

    @PatchMapping("/{id}/reject")
    public GoalInvitationDtoResponse rejectGoalInvitation(@PathVariable("id") @NotNull Long id) {
        return goalInvitationService.rejectGoalInvitation(id);
    }

    @PostMapping("/search")
    public List<GoalInvitationDtoResponse> getInvitations(@RequestBody InvitationFilterDto filter) {
        return goalInvitationService.getInvitations(filter);
    }
}