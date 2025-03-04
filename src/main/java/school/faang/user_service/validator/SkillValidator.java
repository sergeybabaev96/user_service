package school.faang.user_service.validator;

import school.faang.user_service.dto.skill.SkillDto;

public class SkillValidator {
    public static void validateSkillDto(SkillDto skill) {
        if (skill == null || skill.getTitle() == null || skill.getTitle().isBlank()) {
            throw new IllegalArgumentException("Invalid skill : " + skill);
        }
    }
}
