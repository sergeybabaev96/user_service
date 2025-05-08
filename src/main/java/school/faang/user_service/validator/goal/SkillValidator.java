package school.faang.user_service.validator.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.exception.goal.AddedSkillNotExistException;

@Component
public class SkillValidator {

    public void validateExistingSkills(int sillsExist, int expectedSkillsExist) {
        boolean containNotExistingSkill = sillsExist != expectedSkillsExist;
        if (containNotExistingSkill) {
            throw new AddedSkillNotExistException();
        }
    }
}