package school.faang.user_service.filter.goal;

import school.faang.user_service.dto.goal.InvitationFilterIDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.List;

public class GoalInvitationFilter {

    public static List<GoalInvitation> filter(List<GoalInvitation> goalInvitations, InvitationFilterIDto filter) {
        return goalInvitations.stream()
                .filter(goalInvitation -> filter.getInviterId() == null || goalInvitation.getInviter().getId().equals(filter.getInviterId()))
                .filter(goalInvitation -> filter.getInvitedId() == null || goalInvitation.getInvited().getId().equals(filter.getInvitedId()))
                .filter(goalInvitation -> filter.getStatus() == null || goalInvitation.getStatus().equals(filter.getStatus()))
                .toList();
    }
}
