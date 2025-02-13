package school.faang.user_service.service.goal;

import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.filter.goal.GoalFilterDto;

import java.util.List;

public interface GoalService {
    GoalDto createGoal(long userId, GoalDto dto);

    GoalDto updateGoal(long id, GoalDto dto);

    void deleteGoalById(long id);

    List<GoalDto> findSubgoalsByGoalId(long id, GoalFilterDto filters);

    List<GoalDto> findGoalsByUser(long userId, GoalFilterDto filters);
}
