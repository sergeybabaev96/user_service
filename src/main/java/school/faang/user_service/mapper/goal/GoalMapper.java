package school.faang.user_service.mapper.goal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface GoalMapper {

    @Mapping(target = "parent", expression = "java(mapParent(goalDto.parentId()))")
    @Mapping(target = "skillsToAchieve", source = "skills")
    Goal goalDtoToGoal(GoalDto goalDto, List<Skill> skills);

    @Mapping(target = "parentId", source = "parent.id")
    @Mapping(target = "skillIds", expression = "java(mapSkillsToIds(goal.getSkillsToAchieve()))")
    GoalDto goalToGoalDto(Goal goal);

    List<GoalDto> goalListToGoalDtoList(List<Goal> goalList);

    default Goal mapParent(Long parentId) {
        return parentId == null ? null : new Goal(parentId);
    }

    default List<Long> mapSkillsToIds(List<Skill> skills) {
        return skills == null ? Collections.emptyList() : skills.stream()
                .map(Skill::getId)
                .toList();
    }
}
