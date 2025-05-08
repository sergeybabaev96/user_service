package school.faang.user_service.entity.goal.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalDto;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GoalMapperImpl implements GoalMapper {

    @Override
    public Goal dtoToGoal(GoalDto goalDto, Goal parent, List<Skill> skills) {
        Goal goal = new Goal();
        goal.setTitle(goalDto.title());
        goal.setDescription(goalDto.description());
        goal.setParent(parent);
        goal.setSkillsToAchieve(skills);
        return goal;
    }

    @Override
    public GoalDto goalToDto(Goal goal) {
        return new GoalDto(
                goal.getTitle(),
                goal.getDescription(),
                goal.getParent() != null ? goal.getParent().getId() : null,
                goal.getSkillsToAchieve().stream().map(Skill::getId).toList()
        );
    }

}