package school.faang.user_service.filter.goal.invitation;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.filter.Filter;

import java.util.List;

@Component
public class GoalInvitationStatusFilter implements Filter<GoalInvitation, GoalInvitationFilterDto> {
    @Override
    public boolean isApplicable(GoalInvitationFilterDto filters) {
        return filters.getStatus() != null;
    }

    @Override
    public List<GoalInvitation> apply(List<GoalInvitation> goalInvitations, GoalInvitationFilterDto filters) {
        return goalInvitations.stream()
                .filter(f -> f.getStatus().equals(filters.getStatus()))
                .toList();
    }
}
