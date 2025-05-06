package school.faang.user_service.service.goal;

import school.faang.user_service.dto.goal.GoalRequestDto;
import school.faang.user_service.dto.goal.GoalResponseDto;

import java.util.List;

public interface GoalService {
    void createGoal(Long userId,  GoalRequestDto goalRequestDto);
    void deleteGoalById(long goalId);
    List<GoalResponseDto> findSubtasksByParentGoalId(long goalParentId);
}
