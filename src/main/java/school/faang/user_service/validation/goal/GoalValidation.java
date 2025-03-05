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
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GoalValidation {
    private final GoalRepository goalRepository;

    public void validateByCountGoals(Long userId) {
        if (goalRepository.findGoalsByUserId(userId).count() == GoalConstants.MAX_COUNT_GOALS_PER_USER) {
            throw new SkillLimitExceededException(
                    "There can be no more than " + GoalConstants.MAX_COUNT_GOALS_PER_USER + " goals.");
        }
    }

    public void validateByExistsGoal(Optional<Goal> goal) {
        if (goal.isEmpty()) {
            throw new EntityNotFoundException("Goal not found");
        }
    }

    public void validateByCompletionStatus(Optional<Goal> goal) {
        if (goal.isPresent() && goal.get().getStatus() == GoalStatus.COMPLETED) {
            throw new GoalAlreadyCompletedException("Goal is already completed");
        }
    }
}
