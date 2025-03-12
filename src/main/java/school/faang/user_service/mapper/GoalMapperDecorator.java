package school.faang.user_service.mapper;

import lombok.extern.slf4j.Slf4j;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.GoalDataException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@Slf4j
public abstract class GoalMapperDecorator implements GoalMapper {
    @Autowired
    private GoalMapper goalMapper;

    @Autowired
    public GoalRepository goalRepository;

    @Autowired
    public SkillRepository skillRepository;

    @Override
    public GoalDto toDto(Goal goal) {
        GoalDto dto = goalMapper.toDto(goal);
        dto.setParentId(mapParentToParentId(goal));
        dto.setSkillIds(mapSkillsToAchieveToSkillIds(goal.getSkillsToAchieve()));
        return goalMapper.toDto(goal);
    }

    @Override
    public Goal toEntity(GoalDto goalDto) {
        Goal entity = goalMapper.toEntity(goalDto);
        entity.setParent(mapParentIdToParent(goalDto.getParentId()));
        entity.setSkillsToAchieve(mapSkillIdsToSkillsToAchieve(goalDto.getSkillIds()));
        return goalMapper.toEntity(goalDto);
    }

    @Override
    public Goal update(@MappingTarget Goal goal, GoalDto goalDto) {
        Goal updatedGoal = goalMapper.update(goal, goalDto);
        updatedGoal.setParent(mapParentIdToParent(goalDto.getParentId()));
        return goalMapper.update(goal, goalDto);
    }

    private Long mapParentToParentId(Goal goal) {
        return goal.getId();
    }

    public Goal mapParentIdToParent(Long parentId) {
        return goalRepository.findById(parentId)
                .orElseThrow(() -> {
                            log.error("Goal parent not found by id {}", parentId);
                            return new GoalDataException("Goal parent not found by id " + parentId);
                        }
                );
    }

    private List<Long> mapSkillsToAchieveToSkillIds(List<Skill> skillsToAchieve) {
        return skillsToAchieve.stream().map(Skill::getId).toList();
    }

    private List<Skill> mapSkillIdsToSkillsToAchieve(List<Long> skillIds) {
        return skillRepository.findAllById(skillIds);
    }
}
