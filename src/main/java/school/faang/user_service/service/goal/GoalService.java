package school.faang.user_service.service.goal;

import school.faang.user_service.dto.goal.GoalRequestDto;

public interface GoalService {
    void createGoal(Long userId,  GoalRequestDto goalRequestDto);
    void deleteGoalById(long goalId);
}
