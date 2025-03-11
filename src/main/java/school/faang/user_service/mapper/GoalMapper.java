package school.faang.user_service.mapper;

import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.GoalDataException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
@Slf4j
public abstract class GoalMapper {
    @Autowired
    public GoalRepository goalRepository;

    @Autowired
    public SkillRepository skillRepository;

    @Mapping(target = "parentId", source = "parent", qualifiedByName = "mapParentToParentId")
    @Mapping(target = "skillIds", source = "skillsToAchieve", qualifiedByName = "mapSkillsToAchieveToSkillIds")
    public abstract GoalDto toDto(Goal goal);

    @Mapping(target = "parent", source = "parentId", qualifiedByName = "mapParentIdToParent")
    @Mapping(target = "skillsToAchieve", source = "skillIds", qualifiedByName = "mapSkillIdsToSkillsToAchieve")
    public abstract Goal toEntity(GoalDto goalDto);

    @Mapping(target = "id", ignore = true, source = "id")
    @Mapping(target = "parent", source = "parentId", qualifiedByName = "mapParentIdToParent")
    public abstract Goal update(@MappingTarget Goal goal, GoalDto goalDto);

    @Named("mapParentToParentId")
    public Long mapParentToParentId(Goal goal) {
        return goal.getId();
    }

    @Named("mapParentIdToParent")
    public Goal mapParentIdToParent(Long parentId) {
        return goalRepository.findById(parentId)
                .orElseThrow(() -> {
                            log.error("Goal parent not found by id {}", parentId);
                            return new GoalDataException("Goal parent not found by id " + parentId);
                        }
                );
    }

    @Named("mapSkillsToAchieveToSkillIds")
    public List<Long> mapSkillsToAchieveToSkillIds(List<Skill> skillsToAchieve) {
        return skillsToAchieve.stream().map(Skill::getId).toList();
    }

    @Named("mapSkillIdsToSkillsToAchieve")
    public List<Skill> mapSkillIdsToSkillsToAchieve(List<Long> skillIds) {
        return skillRepository.findAllById(skillIds);
    }
}
