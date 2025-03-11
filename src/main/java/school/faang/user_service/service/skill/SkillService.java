package school.faang.user_service.service.skill;

import school.faang.user_service.entity.Skill;

import java.util.List;

public interface SkillService {
    List<Skill> findAllSkillsById(List<Long> skillIds);

    List<Skill> findSkillsByUserId(Long userId);

    List<Skill> findSkillsByGoalId(Long goalId);

    void saveAllSkills(List<Skill> skills);

}
