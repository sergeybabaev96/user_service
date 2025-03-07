package school.faang.user_service.mapper.goal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GoalMapper {

    @Mapping(target = "parent", expression = "java(mapParent(goalDto.parentId()))")
    @Mapping(target = "skillsToAchieve", source = "skills")
    Goal goalDtoToGoal(GoalDto goalDto, List<Skill> skills);

    List<GoalDto> goalListToGoalDtoList(List<Goal> goalList);

    default Goal mapParent(Long parentId) {
        return parentId == null ? null : new Goal(parentId);
    }
}
