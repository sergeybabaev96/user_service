package school.faang.user_service.mapper.goal;

import org.mapstruct.Mapper;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.dto.goal.GoalDto;

import java.util.List;

@Mapper
public interface GoalMapper {
    Goal goalDtoToGoal(GoalDto goalDto);

    List<GoalDto> goalListToGoalDtoList(List<Goal> goalList);
}
