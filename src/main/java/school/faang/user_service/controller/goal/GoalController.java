package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.service.goal.GoalService;

@Controller
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    public Goal createGoal(Long userId, Goal goal) {
        if (!goal.getTitle().isBlank()) throw new IllegalArgumentException("Goal has no title " + goal);
        return goalService.createGoal(userId, goal);
    }
}
