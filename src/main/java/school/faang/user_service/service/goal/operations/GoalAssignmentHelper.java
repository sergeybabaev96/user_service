package school.faang.user_service.service.goal.operations;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class GoalAssignmentHelper {

    private final SkillRepository skillRepository;
    private final GoalRepository goalRepository;

    public void assignSkillsToGoal(Goal goal, List<Long> skillIds) {
        if (skillIds == null || skillIds.isEmpty()) {
            return;
        }
        bindSkillsToGoal(skillIds, goal);
    }

    public void assignSkillsToUsers(Goal goal, List<Long> skillIds) {
        List<User> users = goalRepository.findUsersByGoalId(goal.getId());

        if (users.isEmpty()) {
            return;
        }

        List<Skill> skills = loadSkills(skillIds);
        for (User user : users) {
            user.getSkills().addAll(skills);
        }
    }

    private void bindSkillsToGoal(List<Long> skillIds, Goal goal) {
        if (skillIds == null || skillIds.isEmpty()) {
            return;
        }

        Set<Skill> existingSkills = new HashSet<>(Optional.ofNullable(goal.getSkillsToAchieve()).orElseGet(List::of));
        Set<Skill> newSkills = new HashSet<>(loadSkills(skillIds));

        existingSkills.retainAll(newSkills);
        newSkills.removeAll(existingSkills);

        goal.getSkillsToAchieve().addAll(newSkills);
    }

    private List<Skill> loadSkills(List<Long> skillIds) {
        return Optional.ofNullable(skillIds)
                .filter(ids -> !ids.isEmpty())
                .map(skillRepository::findAllById)
                .orElse(List.of());
    }
}