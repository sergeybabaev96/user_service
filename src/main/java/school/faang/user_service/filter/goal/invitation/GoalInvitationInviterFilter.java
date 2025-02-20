package school.faang.user_service.filter.goal.invitation;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.filter.Filter;

import java.util.List;

@Component
public class GoalInvitationInviterFilter implements Filter<GoalInvitation, GoalInvitationFilterDto> {

    @Override
    public boolean isApplicable(GoalInvitationFilterDto filters) {
        return filters.getInviterNamePattern() != null;
    }

    @Override
    public List<GoalInvitation> apply(List<GoalInvitation> goalInvitations, GoalInvitationFilterDto filters) {
        return goalInvitations.stream()
                .filter(f -> f.getInviter().getUsername().matches(filters.getInviterNamePattern()))
                .toList();
    }
}
