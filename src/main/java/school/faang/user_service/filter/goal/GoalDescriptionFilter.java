package school.faang.user_service.filter.goal;

import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.stream.Stream;

/**
 * Фильтр по описанию цели
 */
@Component
public class GoalDescriptionFilter implements GoalFilter {
    @Override
    public boolean isApplicable(@NotNull GoalFilterDto filter) {
        return filter.getDescription() != null && !filter.getDescription().isBlank();
    }

    @Override
    public Stream<Goal> apply(Stream<Goal> goals, GoalFilterDto filter) {
        return goals.filter(goal -> goal.getDescription()
                .matches("(?i).*" + filter.getDescription() + ".*"));
    }
}
