package school.faang.user_service.dto.goal;

import school.faang.user_service.entity.goal.GoalStatus;

public record SearchGoalDto(
        String title,
        GoalStatus status
) {
}
