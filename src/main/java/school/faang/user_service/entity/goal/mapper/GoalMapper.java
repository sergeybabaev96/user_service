package school.faang.user_service.entity.goal.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.dto.GoalDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GoalMapper {

    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(target = "skillsId", expression = "java(mapSkillsToIds(goal.getSkillsToAchieve()))")
    GoalDto toGoalDto(Goal goal);

    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "skillsToAchieve", ignore = true)
    @Mapping(target = "deadline", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "mentor", ignore = true)
    @Mapping(target = "invitations", ignore = true)
    @Mapping(target = "users", ignore = true)
    Goal toGoal(GoalDto goalDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "skillsToAchieve", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget Goal goal, Goal goalData);

    default List<Long> mapSkillsToIds(List<Skill> skills) {
        return skills.stream()
                .map(Skill::getId)
                .toList();
    }
}
