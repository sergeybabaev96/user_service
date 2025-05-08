package school.faang.user_service.service.goal;

import school.faang.user_service.dto.goal.GoalCreateRequestDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.GoalResponseDto;
import school.faang.user_service.dto.goal.GoalUpdateRequestDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

public interface GoalService {
    GoalResponseDto createGoal(final GoalCreateRequestDto goalCreateRequestDto);
    GoalResponseDto updateGoal(final GoalUpdateRequestDto goalUpdateRequestDto);
    void deleteGoalById(long goalId);
    List<GoalResponseDto> getSubtasksByParentGoalId(long goalParentId);
    List<GoalResponseDto> getGoalsByUser(GoalFilterDto filter);
    Goal getGoalById(long goalId);
    void checkGoalById(long goalId);
}
