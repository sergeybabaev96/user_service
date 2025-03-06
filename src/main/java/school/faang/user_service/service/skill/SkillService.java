package school.faang.user_service.service.skill;

import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.ResponseSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;

import java.util.List;

public interface SkillService {
    ResponseSkillDto create(CreateSkillDto skill);

    List<ResponseSkillDto> getUserSkills(long userId);

    List<SkillCandidateDto> getOfferedSkills(long userId);

    ResponseSkillDto acquireSkillFromOffers(long skillId, long userId);

}
