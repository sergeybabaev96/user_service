package school.faang.user_service.service.goal.operations;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GoalValidator {

    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;


    public Goal findGoalById(Long goalId) {
        if (goalId == null) {
            throw new DataValidationException("Goal ID cannot be null.");
        }
        return goalRepository.findById(goalId)
                .orElseThrow(() -> new DataValidationException("Goal not found with id: " + goalId));
    }

    public Goal findParentGoal(Long parentId) {
        return (parentId != null) ? findGoalById(parentId) : null;
    }

    public void validateActiveGoalsLimit(Long userId) {
        if (userId == null) {
            throw new DataValidationException("User ID cannot be null.");
        }
        int activeGoals = goalRepository.countActiveGoalsPerUser(userId);
        if (activeGoals >= 3) {
            throw new DataValidationException("User cannot have more than 3 active goals.");
        }
    }

    public void validateSkillsExist(List<Long> skillIds) {
        if (skillIds == null || skillIds.isEmpty()) {
            throw new DataValidationException("Skill list cannot be empty.");
        }
        int existingSkills = skillRepository.countExisting(skillIds);
        if (existingSkills != skillIds.size()) {
            throw new DataValidationException("One or more skills do not exist.");
        }
    }

    public void validateGoalUpdatable(Goal goal) {
        if (goal == null) {
            throw new DataValidationException("Goal cannot be null.");
        }
        if (goal.getStatus() == GoalStatus.COMPLETED) {
            throw new DataValidationException("Completed goals cannot be updated.");
        }
    }
}