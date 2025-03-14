package school.faang.user_service.filter.goal;

import school.faang.user_service.dto.goal.SearchGoalDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.stream.Stream;

public interface GoalFilter {
    boolean isApplicable(SearchGoalDto searchGoal);

    Stream<Goal> apply(Stream<Goal> goals, SearchGoalDto searchGoal);
}
