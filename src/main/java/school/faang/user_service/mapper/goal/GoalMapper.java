package school.faang.user_service.mapper.goal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.SkillRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public abstract class GoalMapper {

    protected SkillRepository skillRepository;

    @Mapping(target = "parent", expression = "java(mapParent(goalDto.parentId()))")
    @Mapping(target = "skillsToAchieve", expression = "java(mapSkills(goalDto.skillIds()))")
    public abstract Goal goalDtoToGoal(GoalDto goalDto);

    public abstract List<GoalDto> goalListToGoalDtoList(List<Goal> goalList);

    protected Goal mapParent(Long parentId) {
        if (parentId == null) {
            return null;
        }
        Goal parent = new Goal();
        parent.setId(parentId);
        return parent;
    }

    protected List<Skill> mapSkills(List<Long> skillIds) {
        if (skillIds == null) {
            return Collections.emptyList();
        }
        return skillIds.stream()
                .map(skillRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }
}
