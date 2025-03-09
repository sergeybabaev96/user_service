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

    public GoalDto createGoal(Long userId, GoalDto goalDto) {
        validateUserIdAndGoalDto(userId, goalDto);
        return goalService.createGoal(userId, goalDto);
    }

    public GoalDto updateGoal(Long goalId, GoalDto goalDto) {
        validateId(goalId, "goalId");
        validateTitle(goalDto.getTitle());
        return goalService.updateGoal(goalId, goalDto);
    }

    public void deleteGoal(Long goalId) {
        validateId(goalId, "goalId");
        goalService.deleteGoal(goalId);
    }

    public List<GoalDto> findSubtasksByGoalId(Long goalId, GoalFilterDto filter) {
        validateGoalIdAndFilter(goalId, filter);
        return goalService.findSubtasksByGoalId(goalId, filter);
    }

    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto filter) {
        validateGoalIdAndFilter(userId, filter);
        return goalService.getGoalsByUser(userId, filter);
    }

    private void validateUserIdAndGoalDto(Long userId, GoalDto goalDto) {
        validateId(userId, "userId");
        validateTitle(goalDto.getTitle());
        validateDescription(goalDto.getDescription());
        validateParentIdAndUserId(goalDto.getParentId(), userId);
    }

    private void validateId(Long id, String fieldName) {
        if (id == null || id < 0) {
            throw new DataValidationException(String.format("Invalid %s: %s", fieldName, id));
        }
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new DataValidationException(String.format("Invalid goal title: %s", title));
        }
    }

    private void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new DataValidationException(String.format("Invalid goal description: %s", description));
        }
    }

    private void validateParentIdAndUserId(Long parentId, Long userId) {
        if (parentId == null || parentId < 0) {
            throw new DataValidationException(String.format("Invalid parent id: %s", parentId));
        }
        if (parentId.equals(userId)) {
            throw new DataValidationException("Goal parent id and User id must not be equal");
        }
    }

    private void validateGoalIdAndFilter(Long goalId, GoalFilterDto filter) {
        if (goalId == null || goalId <= 0) {
            throw new DataValidationException("Goal ID must be a positive non-null value.");
        }
        if (filter == null) {
            throw new DataValidationException("Filter cannot be null.");
        }
    }
}