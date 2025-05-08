package school.faang.user_service.filter.goal;

import school.faang.user_service.entity.goal.Goal;

import java.time.LocalDateTime;

public record GoalUpdateBeforeFilter(LocalDateTime updateBefore) implements GoalFilter {
    @Override
    public boolean doFilter(Goal goal) {
        return goal.getUpdatedAt().isBefore(updateBefore);
    }
}
