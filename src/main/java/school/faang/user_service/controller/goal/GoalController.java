package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.skill.SkillService;

@Controller
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;
    private final SkillService skillService;
    private final GoalMapper goalMapper;//todo стоит вынести из контроллера кмк, а то логикой обрастает

    public Goal createGoal(Long userId, Goal goal) {
        if (!goal.getTitle().isBlank()) throw new IllegalArgumentException("Goal has no title");
        return goalService.createGoal(userId, goal);
    }

    public GoalDto updateGoal(Long goalId, GoalDto goalDto) {
        if (!goalDto.getTitle().isBlank()) throw new IllegalArgumentException("Goal has no title");
        Goal updatedGoal = goalService.updateGoal(goalId, goalDto);

        return goalMapper.goalToGoalDTO(updatedGoal);
    }
}
