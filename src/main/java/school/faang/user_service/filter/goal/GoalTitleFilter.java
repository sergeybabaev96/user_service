package school.faang.user_service.filter.goal;

import school.faang.user_service.entity.goal.Goal;

public record GoalTitleFilter(String title) implements GoalFilter {
    @Override
    public boolean doFilter(Goal goal) {
        return goal.getTitle().equals(title);
    }
}
