package school.faang.user_service.filter.goal;

import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;

public record GoalStatusFilter(GoalStatus status) implements GoalFilter {
    @Override
    public boolean doFilter(Goal goal) {
        return goal.getStatus() == status;
    }
}
