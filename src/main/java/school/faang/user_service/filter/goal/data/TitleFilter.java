package school.faang.user_service.filter.goal.data;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.stream.Stream;

@Component
public class TitleFilter implements GoalDataFilter {

    @Override
    public boolean isApplicable(GoalFilterDto filter) {
        return filter.getTitle() != null && !filter.getTitle().isEmpty();
    }

    @Override
    public Stream<Goal> apply(Stream<Goal> goals, GoalFilterDto filter) {
        return goals.filter(goal -> goal.getTitle().contains(filter.getTitle()));
    }
}