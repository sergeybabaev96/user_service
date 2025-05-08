package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.exception.goal.AddedSkillNotExistException;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;

    public void assignSkillToGoal(long goalId, List<Long> skillsId) {
        boolean containNotExistingSkill = skillRepository.countExisting(skillsId) != skillsId.size();
        if(containNotExistingSkill) {
            throw new AddedSkillNotExistException();
        }
        skillsId.forEach(skillId -> skillRepository.assignSkillToGoal(goalId, skillId));
    }

}