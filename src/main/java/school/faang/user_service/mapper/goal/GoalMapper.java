package school.faang.user_service.mapper.goal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface GoalMapper {

    @Mapping(target = "parentId", source = "parent.id")
    @Mapping(target = "skillIds", source = "skillsToAchieve", expression = "java(skillsToIds(skillsToAchieve))")
    GoalDto toDto(Goal goal);

    default List<Long> skillsToIds(List<Skill> skills) {
        if (skills == null) {
            return new ArrayList<>();
        }
        List<Long> skillIds = new ArrayList<>();
        for (Skill skill : skills) {
            skillIds.add(skill.getId());
        }
        return skillIds;
    }

}