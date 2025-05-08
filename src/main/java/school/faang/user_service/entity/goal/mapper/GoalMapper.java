package school.faang.user_service.entity.goal.mapper;

import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalDto;

import java.util.List;

public interface GoalMapper {
    Goal dtoToGoal(GoalDto goalDto, Goal parent, List<Skill> skills);
    GoalDto goalToDto(Goal goal);
}