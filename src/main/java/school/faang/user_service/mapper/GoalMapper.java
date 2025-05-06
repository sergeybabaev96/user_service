package school.faang.user_service.mapper;

import org.mapstruct.*;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.skill.SkillService;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalMapper {

    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(target = "skillIds", expression = "java(goalSkillsToSkillIDs(goal))")
    GoalDto goalToGoalDTO(Goal goal);

    @Mapping(source = "parentId", target = "parent", qualifiedByName = "mapParent")
    @Mapping(source = "skillIds", target = "skillsToAchieve", qualifiedByName = "mapSkills")
    Goal goalDtoToGoal(GoalDto goalDto, @Context GoalService goalService, @Context SkillService skillService);

    default List<Long> goalSkillsToSkillIDs(Goal goal) {
        return goal.getSkillsToAchieve().stream()
                .map(Skill::getId)
                .toList();
    }

    void updateGoalFromDto(GoalDto dto, @MappingTarget Goal goal);

    @Named("mapParent")
    default Goal mapParent(Long id, @Context GoalService goalService) {
        return id != null ? goalService.findById(id) : null;
    }

    @Named("mapSkills")
    default List<Skill> mapSkills(List<Long> ids, @Context SkillService skillService) {
        return ids == null ? List.of() : ids.stream()
                .map(skillService::findById)
                .toList();
    }
}
