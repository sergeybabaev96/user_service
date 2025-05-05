package school.faang.user_service.controller;

import school.faang.user_service.dto.skill.SkillDto;

public class SkillController {
    public SkillDto create(SkillDto skill) {
        if (validateSkill(skill)) {
            return skill;
        }
        return skill;
    }

    public boolean validateSkill(SkillDto skill) {
        if (skill == null || skill.getTitle().isEmpty() || skill.getTitle().isBlank()) {
            throw new IllegalArgumentException("Invalid skill title");
        }
        return true;
    }
}
