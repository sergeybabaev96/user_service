package school.faang.user_service.entity.goal.mapper;

import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalDto;

public interface GoalMapper {
    Goal dtoToGoal(GoalDto goalDto);
    GoalDto goalToDto(Goal goal);
}