package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.service.goal.GoalService;

@RestController
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    public void createGoal(Long userId, Goal goal) {
        if (goal.getTitle().isBlank()) {
            throw new IllegalArgumentException("Goal hasn't title");
        }
        goalService.createGoal(userId, goal);
    }

    public void deleteGoal(Long goalId) {
        goalService.deleteGoal(goalId);
    }

    public void updateGoal(Long goalId, GoalDto goal) {
        if (goal.title().isBlank()) {
            throw new IllegalArgumentException("Goal hasn't title");
        }
        goalService.updateGoal(goalId, goal);
    }
}
