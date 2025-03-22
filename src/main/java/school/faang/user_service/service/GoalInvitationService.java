package school.faang.user_service.service;

import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;

import java.util.List;

public interface GoalInvitationService {

    void createInvitation(GoalInvitationDto invitationDto);

    void acceptGoalInvitation(Long id);

    void rejectGoalInvitation(Long id);

    List<GoalInvitationDto> getInvitations(InvitationFilterDto filter);

    List<GoalInvitationDto> getInvitationsByInvitedUserId(Long invitedUserId);
}