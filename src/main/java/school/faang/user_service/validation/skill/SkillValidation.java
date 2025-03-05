package school.faang.user_service.validation.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.SkillRepository;
import java.util.HashSet;

@Component
@RequiredArgsConstructor
public class SkillValidation {
    private final SkillRepository skillRepository;

    public void validateByExistsGoalSkills(Goal goal) {
        boolean isContained = new HashSet<>(goal.getSkillsToAchieve()).containsAll(skillRepository.findAll());
        if (!isContained) {
            throw new IllegalArgumentException("Goal contains non-existent skills");
        }
    }
}
