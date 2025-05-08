package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;
    private final GoalMapper goalMapper;

    public GoalDto createGoal(Long userId, Goal goal) {
        if (!goal.getTitle().isBlank()) throw new IllegalArgumentException("Goal has no title");
        Goal createdGoal = goalService.createGoal(userId, goal);
        return goalMapper.goalToGoalDTO(createdGoal);
    }

    public GoalDto updateGoal(Long goalId, GoalDto goalDto) {
        if (!goalDto.getTitle().isBlank()) throw new IllegalArgumentException("Goal has no title");
        Goal updatedGoal = goalService.updateGoal(goalId, goalDto);

        return goalMapper.goalToGoalDTO(updatedGoal);
    }

    public GoalDto deleteGoal(long goalId) {
        Goal deletedGoal = goalService.deleteGoal(goalId);
        return goalMapper.goalToGoalDTO(deletedGoal);
    }

    public List<GoalDto> findSubtasksByGoalId(long goalId) {
        List<Goal> subtasksByGoalId = goalService.findSubtasksByGoalId(goalId);
        return goalMapper.mapGoalsToDTOs(subtasksByGoalId);
    }

    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto filter) {
        List<Goal> filteredUsersGoals = goalService.findGoalsByUserId(userId, filter);
        return goalMapper.mapGoalsToDTOs(filteredUsersGoals);
    }
}
