package school.faang.user_service.service.skill;

import school.faang.user_service.entity.Skill;

import java.util.List;

public interface SkillService {
    void checkSkillById(long skillId);

    Skill getSkillById(long skillId);

    void assignSkillsToUsers(List<Long> skillIds, List<Long> userIds);
}
