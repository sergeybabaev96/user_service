package school.faang.user_service.dto.goal;

import lombok.Builder;
import school.faang.user_service.entity.goal.GoalStatus;
import java.util.List;

@Builder
public record GoalDto(
        Long id,
        String description,
        Long parentId,
        String title,
        GoalStatus status,
        List<Long> skillIds,
        List<Long> userIds
) {
}
