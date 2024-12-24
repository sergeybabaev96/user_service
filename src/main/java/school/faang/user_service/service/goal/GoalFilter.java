package school.faang.user_service.service.goal;

import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.stream.Stream;

public interface GoalFilter {
    /**
     * Check if the given {@code filters} are applicable to this filter.
     *
     * @param filters the filters to check
     * @return whether the filters are applicable
     */
    boolean isApplicable(GoalFilterDto filters);

    /**
     * Filters the given stream of goals based on the specified filters.
     *
     * @param goals   the stream of goals to be filtered
     * @param filters the criteria used to filter the goals
     * @return a stream of goals that match the filter criteria
     */
    Stream<Goal> apply(Stream<Goal> goals, GoalFilterDto filters);
}