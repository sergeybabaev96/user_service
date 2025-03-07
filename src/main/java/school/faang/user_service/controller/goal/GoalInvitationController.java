package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.service.goal.GoalInvitationService;
import java.util.List;

@RestController
@RequestMapping("/api/goal-invitations")
@Slf4j
@RequiredArgsConstructor
public class GoalInvitationController {

    private final GoalInvitationService goalInvitationService;

    @PostMapping
    public void createInvitation(@RequestBody GoalInvitationDto invitationDto) {
        log.info("Received request to create invitation: {}", invitationDto);
        goalInvitationService.createInvitation(invitationDto);
    }

    @PutMapping("/{id}/accept")
    public void acceptGoalInvitation(@PathVariable Long id) {
        log.info("Received request to accept invitation with ID: {}", id);
        goalInvitationService.acceptGoalInvitation(id);
    }

    @PutMapping("/{id}/reject")
    public void rejectGoalInvitation(@PathVariable Long id) {
        log.info("Received request to reject invitation with ID: {}", id);
        goalInvitationService.rejectGoalInvitation(id);
    }

    @GetMapping
    public List<GoalInvitationDto> getInvitations(@RequestBody InvitationFilterDto filter) {
        log.info("Received request to fetch invitations with filter: {}", filter);
        return goalInvitationService.getInvitations(filter);
    }
}