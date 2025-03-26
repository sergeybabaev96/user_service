package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.stream.Stream;

@Component
public class GoalTitleFilter extends GoalFilter {

    public GoalTitleFilter() {
        super();
    }

    @Override
    public Stream<Goal> apply(Stream<Goal> goals, GoalFilterDto filter) {
        return filter == null ? goals :
                apply(goals, Goal::getTitle, filter.getTitlePattern());
    }
}