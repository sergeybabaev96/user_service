package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.validator.goal.SkillValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;
    private final SkillValidator skillValidator;

    public void assignSkillToGoal(long goalId, List<Long> skillsId) {
        List<Long> absentSkillsId = skillsId.stream()
                .filter(this::isSkillNotExists)
                .toList();
        skillValidator.validateExistingSkills(absentSkillsId);
        skillsId.forEach(skillId -> skillRepository.assignSkillToGoal(goalId, skillId));
    }

    public void updateSkillForGoal(long goalId, List<Long> newSkillsId) {
        skillRepository.removeSkillsFromGoal(goalId);
        assignSkillToGoal(goalId, newSkillsId);
    }

    public void assignSkillsToUser(long userId, List<Skill> skills) {
        List<Skill> ownedSkills = skillRepository.findAllByUserId(userId);
        skills
                .stream()
                .filter(skill -> !ownedSkills.contains(skill))
                .forEach(skill -> skillRepository.assignSkillToUser(skill.getId(), userId));
    }

    public boolean isSkillNotExists(long skillId) {
        return !skillRepository.existsById(skillId);
    }

    public List<Skill> findSkillsByGoalId(long goalId) {
        return skillRepository.findSkillsByGoalId(goalId);
    }
}