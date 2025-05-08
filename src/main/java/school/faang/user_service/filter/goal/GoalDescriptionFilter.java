package school.faang.user_service.filter.goal;

import school.faang.user_service.entity.goal.Goal;

public record GoalDescriptionFilter(String description) implements GoalFilter {
    @Override
    public boolean doFilter(Goal goal) {
        return goal.getDescription().contains(description);
    }
}
