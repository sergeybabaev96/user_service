package school.faang.user_service.dto.goal;

import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record SearchGoalDto(
        String title,
        GoalStatus status,
        LocalDateTime updatedAt,
        List<Long> userIds,
        List<Long> skillIds
) {
    public SearchGoalDto {
        userIds = new ArrayList<>();
        skillIds = new ArrayList<>();
    }
}
