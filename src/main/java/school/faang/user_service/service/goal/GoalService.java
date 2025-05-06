package school.faang.user_service.service.goal;

import school.faang.user_service.dto.goal.GoalDto;

public interface GoalService {
    void createGoal(Long userId,  GoalDto goalDto);
}
