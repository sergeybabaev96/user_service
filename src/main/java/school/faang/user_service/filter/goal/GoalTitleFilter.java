package school.faang.user_service.filter.goal;


import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;
import java.util.stream.Stream;

public class GoalTitleFilter implements GoalFilter {

    @Override
    public boolean isApplicable(GoalFilterDto filter) {
        return filter != null && filter.getTitlePattern() != null && !filter.getTitlePattern().isEmpty();
    }

    @Override
    public List<Goal> apply(GoalFilterDto dto, Stream<Goal> goals) {
        return List.of();
    }
}
