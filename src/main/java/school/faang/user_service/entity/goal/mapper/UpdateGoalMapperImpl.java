package school.faang.user_service.entity.goal.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.dto.UpdateGoalDto;

@Component
@RequiredArgsConstructor
public class UpdateGoalMapperImpl implements GoalMapper<UpdateGoalDto> {

    @Override
    public Goal dtoToGoal(UpdateGoalDto updateGoalDto) {
        Goal goal = new Goal();
        goal.setTitle(updateGoalDto.title());
        goal.setDescription(updateGoalDto.description());
        goal.setStatus(updateGoalDto.status());
        return goal;
    }

    @Override
    public UpdateGoalDto goalToDto(Goal goal) {
        return new UpdateGoalDto(
                goal.getTitle(),
                goal.getDescription(),
                goal.getStatus(),
                goal.getSkillsToAchieve().stream().map(Skill::getId).toList()
        );
    }
}