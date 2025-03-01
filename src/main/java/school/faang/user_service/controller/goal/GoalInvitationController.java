package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.service.GoalInvitationService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GoalInvitationController {

    private final GoalInvitationService goalInvitationService;

    public GoalInvitationDto createInvitation(GoalInvitationDto dto){
        return goalInvitationService.createInvitation(dto);
    }

    public GoalInvitationDto acceptGoalInvitation(Long id){
        return goalInvitationService.acceptGoalInvitation(id);
    }

    public GoalInvitationDto rejectGoalInvitation(Long id){
        return goalInvitationService.rejectGoalInvitation(id);
    }

    public List<GoalInvitationDto> getInvitations(InvitationFilterDto filters){
        return goalInvitationService.getInvitations(filters);
    }
}
