package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.service.goal.GoalInvitationService;

import java.util.List;

@RestController
@RequestMapping("/goal-invitations")
@RequiredArgsConstructor
public class GoalInvitationController {
    private final GoalInvitationService goalInvitationService;

    @PostMapping("/create")
    public void createInvitation(@Validated @RequestBody GoalInvitationDto invitation) {
        goalInvitationService.createInvitation(invitation);
    }

    @PutMapping("/accept-invitation/{id}")
    public void acceptGoalInvitation(@PathVariable long id) {
        goalInvitationService.acceptInvitation(id);
    }

    @PutMapping("/decline-invitation/{id}")
    public void rejectGoalInvitation(@PathVariable long id) {
        goalInvitationService.rejectGoalInvitation(id);
    }

    @GetMapping("/get-goal-invitations")
    public List<GoalInvitationDto> getInvitations(@Validated @RequestBody GoalInvitationFilterDto filter) {
        return goalInvitationService.getInvitations(filter);
    }

}