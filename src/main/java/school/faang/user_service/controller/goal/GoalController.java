package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.SearchGoalDto;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    public void createGoal(Long userId, GoalDto goal) {
        validateByTitle(goal.title());
        goalService.createGoal(userId, goal);
    }

    public void deleteGoal(Long goalId) {
        goalService.deleteGoal(goalId);
    }

    public void updateGoal(Long goalId, GoalDto goal) {
        validateByTitle(goal.title());
        goalService.updateGoal(goalId, goal);
    }

    public List<GoalDto> findSubtasksByGoalId(Long goalId, SearchGoalDto searchGoalDto) {
        return goalService.findSubtasksByGoalId(goalId, searchGoalDto);
    }

    public List<GoalDto> getGoalsByUserId(Long userId, SearchGoalDto searchGoalDto) {
        return goalService.getGoalsByUserId(userId, searchGoalDto);
    }

    private void validateByTitle(String title) {
        if (title.isBlank()) {
            throw new IllegalArgumentException("Goal hasn't title");
        }
    }
}
