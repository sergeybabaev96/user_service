package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.SearchGoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.validation.goal.GoalValidation;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;
    private final GoalValidation goalValidation;

    public void createGoal(Long userId, Goal goal) {
        goalValidation.validateByTitle(goal.getTitle());
        goalService.createGoal(userId, goal);
    }

    public void deleteGoal(Long goalId) {
        goalService.deleteGoal(goalId);
    }

    public void updateGoal(Long goalId, GoalDto goal) {
        goalValidation.validateByTitle(goal.title());
        goalService.updateGoal(goalId, goal);
    }

    public List<GoalDto> findSubtasksByGoalId(Long goalId, SearchGoalDto searchGoalDto) {
        return goalService.findSubtasksByGoalId(goalId, searchGoalDto);
    }

    public List<GoalDto> getGoalsByUserId(Long userId, SearchGoalDto searchGoalDto) {
        return goalService.getGoalsByUserId(userId, searchGoalDto);
    }
}
