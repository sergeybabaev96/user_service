package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.SearchGoalDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.stream.Stream;

@Component
public class GoalTitleFilter implements GoalFilter {

    @Override
    public boolean isApplicable(SearchGoalDto searchGoalDto) {
        return searchGoalDto.title() != null;
    }

    @Override
    public Stream<Goal> apply(Stream<Goal> goals, SearchGoalDto searchGoalDto) {
        return goals
                .filter(goal -> searchGoalDto.title().equalsIgnoreCase(goal.getTitle()));
    }
}
