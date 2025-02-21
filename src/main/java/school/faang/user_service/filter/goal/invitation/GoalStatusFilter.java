package school.faang.user_service.filter.goal.invitation;

import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.filter.Filter;
import school.faang.user_service.filter.goal.GoalFilterDto;

import java.util.List;

public class GoalStatusFilter implements Filter<Goal, GoalFilterDto> {
    @Override
    public boolean isApplicable(GoalFilterDto dto) {
        return dto.getStatus() != null;
    }

    @Override
    public List<Goal> apply(List<Goal> goals, GoalFilterDto filters) {
        return goals.stream()
                .filter(f -> f.getStatus().equals(filters.getStatus()))
                .toList();
    }
}
