package school.faang.user_service.service.goal;

import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.filter.goal.invitation.GoalInvitationFilterDto;

import java.util.List;

public interface GoalInvitationService {
    void createInvitation(GoalInvitationDto goalInvitationDto);

    void acceptGoalInvitation(long goalInvitationId);

    void rejectGoalInvitation(long goalInvitationId);

    List<GoalInvitationDto> getInvitationsWithFilters(GoalInvitationFilterDto filters);
}
