package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    public GoalDto createGoal(long userId, GoalDto goalDto) {
        validateGoalDto(goalDto);
        return goalService.createGoal(userId, goalDto);
    }

    public GoalDto updateGoal(long goalId, GoalDto goalDto) {
        validateTitle(goalDto.getTitle());
        return goalService.updateGoal(goalId, goalDto);
    }

    public void deleteGoal(long goalId) {
        goalService.deleteGoal(goalId);
    }

    public List<GoalDto> findSubtasksByGoalId(long goalId, GoalFilterDto filter) {
        validateFilter(filter);
        return goalService.findSubtasksByGoalId(goalId, filter);
    }

    public List<GoalDto> getGoalsByUser(long userId, GoalFilterDto filter) {
        validateFilter(filter);
        return goalService.getGoalsByUser(userId, filter);
    }

    private void validateGoalDto(GoalDto goalDto) {
        validateTitle(goalDto.getTitle());
        validateDescription(goalDto.getDescription());
        validateParentId(goalDto.getParentId());
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new DataValidationException("Invalid goal title: " + title);
        }
    }

    private void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new DataValidationException("Invalid goal description: " + description);
        }
    }

    private void validateParentId(long parentId) {
        if (parentId < 0) {
            throw new DataValidationException("Invalid parent id: " + parentId);
        }
    }

    private void validateFilter(GoalFilterDto filter) {
        if (filter == null) {
            throw new DataValidationException("Filter cannot be null.");
        }
    }
}