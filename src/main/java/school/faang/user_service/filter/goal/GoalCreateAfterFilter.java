package school.faang.user_service.filter.goal;

import school.faang.user_service.entity.goal.Goal;

import java.time.LocalDateTime;

public record GoalCreateAfterFilter(LocalDateTime createAfter) implements GoalFilter {
    @Override
    public boolean doFilter(Goal goal) {
        return goal.getCreatedAt().isAfter(createAfter);
    }
}
