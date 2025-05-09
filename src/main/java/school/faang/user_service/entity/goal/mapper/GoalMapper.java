package school.faang.user_service.entity.goal.mapper;

import school.faang.user_service.entity.goal.Goal;

public interface GoalMapper<T> {
    Goal dtoToGoal(T createGoalDto);
    T goalToDto(Goal goal);
}