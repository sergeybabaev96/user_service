package school.faang.user_service.filter.goal;

import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.function.Function;
import java.util.stream.Stream;

public abstract class GoalFilter {

    public abstract Stream<Goal> apply(Stream<Goal> goals, GoalFilterDto filter);

    private boolean isApplicable(Stream<Goal> goals, String pattern) {
        return goals != null && isValidString(pattern);
    }

    protected Stream<Goal> apply(Stream<Goal> goals, Function<Goal, String> fieldMapper, String pattern) {
        return !isApplicable(goals, pattern) ? goals :
                goals.filter(
                        goal -> goal != null &&
                        isValidString(fieldMapper.apply(goal)) &&
                        fieldMapper.apply(goal).contains(pattern));
    }

    private boolean isValidString(String string) {
        return string != null && !string.isBlank();
    }
}
