package school.faang.user_service.entity.goal.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.dto.CreateGoalDto;

@Component
@RequiredArgsConstructor
public class CreateGoalMapperImpl implements GoalMapper<CreateGoalDto> {

    @Override
    public Goal dtoToGoal(CreateGoalDto createGoalDto) {
        Goal goal = new Goal();
        goal.setId(createGoalDto.id());
        goal.setTitle(createGoalDto.title());
        goal.setDescription(createGoalDto.description());
        return goal;
    }

    @Override
    public CreateGoalDto goalToDto(Goal goal) {
        return new CreateGoalDto(
                goal.getId(),
                goal.getTitle(),
                goal.getDescription(),
                goal.getParent() != null ? goal.getParent().getId() : null,
                goal.getSkillsToAchieve().stream().map(Skill::getId).toList()
        );
    }

}