package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.SearchGoalDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class GoalStatusFilter implements GoalFilter {
    @Override
    public boolean isApplicable(SearchGoalDto searchGoalDto) {
        return Objects.nonNull(searchGoalDto.status());
    }

    @Override
    public Stream<Goal> apply(Stream<Goal> goals, SearchGoalDto searchGoalDto) {
        return goals.filter(goal -> searchGoalDto.status().equals(goal.getStatus()));
    }
}
