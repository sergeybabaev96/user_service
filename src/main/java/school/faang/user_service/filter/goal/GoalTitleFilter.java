package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.filter.Filter;

import java.util.List;

@Component
public class GoalTitleFilter implements Filter<Goal, GoalFilterDto> {

    @Override
    public boolean isApplicable(GoalFilterDto dto) {
        return dto.getTitle() != null;
    }

    @Override
    public List<Goal> apply(List<Goal> goals, GoalFilterDto filters) {
        return goals.stream()
                .filter(f -> f.getTitle().equals(filters.getTitle()))
                .toList();
    }
}
