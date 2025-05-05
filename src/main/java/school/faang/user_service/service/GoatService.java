package school.faang.user_service.service;

import school.faang.user_service.dto.GoalDto;

public interface GoatService {
    void createGoal(Long userId,  GoalDto goalDto);
}
