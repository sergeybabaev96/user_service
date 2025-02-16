package school.faang.user_service.controller.skill;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.service.skill.SkillService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Validated
public class SkillController {

    private final SkillService skillService;
    private final SkillMapper skillMapper;

    public SkillDto create(SkillDto skillDto) {
        Skill skill = skillService.create(skillMapper.toEntity(skillDto));
        return skillMapper.toDto(skill);
    }

    public List<SkillDto> getUserSkills(
            @Min(value = 1, message = "User id must be greater than 0") long userId) {
        return skillService.getUserSkills(userId)
                .stream()
                .map(skillMapper::toDto).toList();
    }

    public List<SkillCandidateDto> getOfferedSkills(
            @Min(value = 1, message = "User id must be greater than 0") long userId) {
        return skillService.getOfferedSkills(userId).entrySet().stream()
                .map(entry ->
                        new SkillCandidateDto(
                                skillMapper.toDto(entry.getKey()), entry.getValue()))
                .toList();
    }

    public SkillDto acquireSkillFromOffers(
            @Min(value = 1, message = "Skill id must be greater than 0") long skillId,
            @Min(value = 1, message = "User id must be greater than 0") long userId) {
        Skill skill = skillService.acquireSkillFromOffers(skillId, userId);
        return skillMapper.toDto(skill);
    }
}
