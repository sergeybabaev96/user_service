package school.faang.user_service.utils;

import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;

public class ValidationUtils {
    public static void validateSkill(SkillDto skill) {
        if (skill == null || skill.title().isBlank()) {
            throw new DataValidationException("Title doesn't allow be empty");}

    }
}
