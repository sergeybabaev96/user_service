package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.goal.GoalService;


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

    private void validateUserIdAndGoalDto(Long userId, GoalDto goalDto) {
        validateId(userId, "userId");
        validateTitle(goalDto.getTitle());

        if (goalDto.getDescription() == null || goalDto.getDescription().isBlank()) {
            throw new DataValidationException("invalid goal description " + goalDto.getDescription());
        }
        if (goalDto.getParentId() == null || goalDto.getParentId() < 0) {
            throw new DataValidationException("invalid parent id " + goalDto.getParentId());
        }
        if (goalDto.getParentId().equals(userId)) {
            throw new DataValidationException("Goal parent id and User id, don't have to be equals ");
        }
    }

    private void validateId(Long id, String name) {
        if (id == null || id < 0) {
            throw new DataValidationException(String.format("invalid %s " + id, name));
        }
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new DataValidationException("invalid goal title " + title);
        }
    }
}