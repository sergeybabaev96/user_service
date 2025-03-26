package school.faang.user_service.service;

import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

import java.util.List;

public interface SkillService {
    boolean doesSkillExists(long skillId);

    List<Skill> findSkillsByUserId(long userId);

    SkillDto create(SkillDto skillDto);

    List<SkillDto> getUserSkills(Long userId);

    List<SkillCandidateDto> getOfferedSkills(Long userId);

    SkillDto acquireSkillFromOffers(long skillId, long userId);
}
