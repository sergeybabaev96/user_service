package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.SearchGoalDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.stream.Stream;

@Component
public class GoalTitleFilter implements GoalFilter {

    @Override
    public boolean isApplicable(SearchGoalDto searchGoal) {
        return !searchGoal.title().isBlank();
    }

    @Override
    public Stream<Goal> apply(Stream<Goal> goals, SearchGoalDto searchGoal) {
        return goals.filter(goal -> searchGoal.title().equals(goal.getTitle()));
    }
}
