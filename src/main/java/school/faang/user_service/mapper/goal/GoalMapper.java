package school.faang.user_service.mapper.goal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface GoalMapper {

    @Mapping(target = "parentId", source = "parent.id")
    @Mapping(target = "skillIds", expression = "java(skillsToIds(goal.getSkillsToAchieve()))")
    GoalDto toDto(Goal goal);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "mentor", ignore = true)
    @Mapping(target = "invitations", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "skillsToAchieve", ignore = true)
    void updateGoalFromDto(GoalDto goalDto, @MappingTarget Goal goal);

    default List<Long> skillsToIds(List<Skill> skills) {
        return skills == null ? new ArrayList<>() :
                skills.stream().map(Skill::getId).toList();
    }
}