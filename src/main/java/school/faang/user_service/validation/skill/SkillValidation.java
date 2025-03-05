package school.faang.user_service.validation.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.validation.ExceptionHandling;

import java.util.HashSet;

@Component
@RequiredArgsConstructor
public class SkillValidation {
    private final SkillRepository skillRepository;
    private final ExceptionHandling exceptionHandling;

    public void validateByExistsGoalSkills(Goal goal) {
        try {
            boolean isContained = new HashSet<>(goal.getSkillsToAchieve()).containsAll(skillRepository.findAll());
            if (!isContained) {
                throw new IllegalArgumentException("Goal contains non-existent skills");
            }
        } catch (IllegalArgumentException e) {
            exceptionHandling.handleException(e);
        }
    }
}
