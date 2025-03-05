package school.faang.user_service.validation.goal;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.constants.goal.GoalConstants;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.goal.GoalAlreadyCompletedException;
import school.faang.user_service.exception.skill.SkillLimitExceededException;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validation.ExceptionHandling;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GoalValidation {
    private final GoalRepository goalRepository;
    private final ExceptionHandling exceptionHandling;

    public void validateByCountGoals(Long userId) {
        try {
            if (goalRepository.findGoalsByUserId(userId).count() == GoalConstants.MAX_COUNT_GOALS_PER_USER) {
                throw new SkillLimitExceededException(
                        "There can be no more than " + GoalConstants.MAX_COUNT_GOALS_PER_USER + " goals.");
            }
        } catch (SkillLimitExceededException e) {
            exceptionHandling.handleException(e);
        }
    }

    public void validateByExistsGoal(Optional<Goal> goal) {
        try {
            if (goal.isEmpty()) {
                throw new EntityNotFoundException("Goal not found");
            }
        } catch (EntityNotFoundException e) {
            exceptionHandling.handleException(e);
        }
    }

    public void validateByCompletionStatus(Optional<Goal> goal) {
        try {
            if (goal.isPresent() && goal.get().getStatus() == GoalStatus.COMPLETED) {
                throw new GoalAlreadyCompletedException("Goal is already completed");
            }
        } catch (GoalAlreadyCompletedException e) {
            exceptionHandling.handleException(e);
        }
    }

    public void validateByTitle(String title) {
        try {
            if (title.isBlank()) {
                throw new IllegalArgumentException("Goal hasn't title");
            }
        } catch (IllegalArgumentException e) {
            exceptionHandling.handleException(e);
        }
    }

    public void validateByExistsGoalOnId(Long goalId) {
        try {
            if (!goalRepository.existsById(goalId)) {
                throw new EntityNotFoundException("Goal not found");
            }
        } catch (EntityNotFoundException e) {
            exceptionHandling.handleException(e);
        }
    }
}
