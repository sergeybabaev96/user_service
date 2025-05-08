package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.validator.goal.SkillValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;
    private final SkillValidator skillValidator;

    public void assignSkillToGoal(long goalId, List<Long> skillsId) {
        skillValidator.validateExistingSkills(skillRepository.countExisting(skillsId), skillsId.size());
        skillsId.forEach(skillId -> skillRepository.assignSkillToGoal(goalId, skillId));
    }
}