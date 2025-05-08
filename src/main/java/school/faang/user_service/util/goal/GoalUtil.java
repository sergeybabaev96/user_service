package school.faang.user_service.util.goal;

import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.filter.goal.GoalFilter;

import java.time.LocalDateTime;

public class GoalUtil {

    public static void updateTime(Goal goalToUpdate, LocalDateTime time) {
        goalToUpdate.setUpdatedAt(time);
    }

    public static boolean goalFilter(Goal goal, GoalFilterDto filterDto) {

        return GoalFilter.createFilters(filterDto).stream()
                .allMatch(goalFilter -> goalFilter.doFilter(goal));
    }
}
