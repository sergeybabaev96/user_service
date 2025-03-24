package school.faang.user_service.filter.goal.filters;

import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.filter.goal.GoalFilter;

import java.util.stream.Stream;

public class TestGoalStatusFilter implements GoalFilter {

    @Override
    public boolean isApplicable(GoalFilterDto filter) {
        return true;
    }

    @Override
    public Stream<Goal> apply(Stream<Goal> goals, GoalFilterDto filter) {
        return goals.filter(goal -> goal.getStatus() == filter.getStatus());
    }
}
