package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {
    private final SkillRepository skillRepository;

    public List<Skill> findAllSkillsById(List<Long> skillIds) {
        return skillRepository.findAllById(skillIds);
    }

    public List<Skill> findSkillsByUserId(Long userId) {
        return skillRepository.findAllByUserId(userId);
    }

    public List<Skill> findSkillsByGoalId(Long goalId) {
        return skillRepository.findSkillsByGoalId(goalId);
    }

    public void saveAllSkills(List<Skill> skills) {
        skillRepository.saveAll(skills);
    }
}
