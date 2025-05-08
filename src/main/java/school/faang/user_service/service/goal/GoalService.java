package school.faang.user_service.service.goal;

import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.GoalRequestDto;
import school.faang.user_service.dto.goal.GoalResponseDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

public interface GoalService {
    void createGoal(Long userId, final GoalRequestDto goalRequestDto);
    GoalResponseDto updateGoal(long goalId, final GoalRequestDto goalRequestDto);
    void deleteGoalById(long goalId);

    @Transactional(readOnly = true)
    List<GoalResponseDto> getSubtasksByParentGoalId(long goalParentId);

    List<GoalResponseDto> getGoalsByUser(Long userId, GoalFilterDto filter);

    Goal getGoalById(long goalId);

    void checkGoalById(long goalId);
}
