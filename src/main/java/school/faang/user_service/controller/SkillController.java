package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    public SkillDto create(SkillDto skillDto) {
        validateSkillDtoTitle(skillDto);
        return skillService.create(skillDto);
    }

    public List<SkillDto> getUserSkills(long userId) {
        return skillService.getUserSkills(userId);
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        return skillService.getOfferedSkills(userId);
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        return skillService.acquireSkillFromOffers(skillId, userId);
    }

    private void validateSkillDtoTitle(SkillDto skill) {
        if (skill.title() == null || skill.title().isBlank()) {
            throw new DataValidationException("Invalid skill : " + skill);
        }
    }
}
