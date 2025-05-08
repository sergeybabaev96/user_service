package school.faang.user_service.service;

import org.jetbrains.annotations.NotNull;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;

public interface GoalService {
    Goal createGoal(Long userId, Goal goal);

    Goal updateGoal(Long goalId, GoalDto goalDto);

    Goal deleteGoal(long goalId);

    List<Goal> findSubtasksByGoalId(long goalId);

    List<Goal> findSubtasksByGoalId(long goalId, GoalFilterDto filter);

    List<Goal> findGoalsByUserId(Long userId, GoalFilterDto filter);

    Goal findById(Long id);

    public static boolean goalIsActive(@NotNull Goal goal) {
        return GoalStatus.ACTIVE == goal.getStatus();
    }

    public static boolean goalIsCompleted(@NotNull Goal goal) {
        return GoalStatus.COMPLETED == goal.getStatus();
    }
}
