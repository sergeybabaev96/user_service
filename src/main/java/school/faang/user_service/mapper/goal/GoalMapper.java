package school.faang.user_service.mapper.goal;

import org.mapstruct.Mapper;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.dto.goal.GoalDto;

@Mapper
public interface GoalMapper {
    GoalDto goalToGoalDto(Goal goal);
    Goal goalDtoToGoal(GoalDto goalDto);
}
