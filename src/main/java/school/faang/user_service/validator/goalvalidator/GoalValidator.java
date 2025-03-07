package school.faang.user_service.validator.goalvalidator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class GoalValidator {
    public void validateTitle(Goal goal) {
        if (goal.getTitle().isBlank()) {
            log.error("Title can not be null or empty.");
            throw new IllegalArgumentException();
        }
    }

    public void validateSkillsToAchieve(Goal goal) {
        if (Objects.isNull(goal.getSkillsToAchieve()) || goal.getSkillsToAchieve().isEmpty()) {
            log.error("Can not create goal without skills.");
            throw new IllegalArgumentException();
        }
    }

    public void validateSkillsExistInBase(List<Skill> goalSkills, List<Skill> existingSkills) {
        if (!new HashSet<>(existingSkills).containsAll(goalSkills)) {
            log.error("Goal skills must be already exist.");
            throw new IllegalStateException();
        }
    }
}
