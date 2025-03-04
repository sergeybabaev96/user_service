package school.faang.user_service.validator;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;

@Component
public class SkillValidator {
    public void validateSkillDto(SkillDto skill) {
        if (skill.getTitle() == null || skill.getTitle().isBlank()) {
            throw new DataValidationException("Invalid skill : " + skill);
        }
    }
}
