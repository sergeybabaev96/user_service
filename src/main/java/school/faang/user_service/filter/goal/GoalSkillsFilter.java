package school.faang.user_service.filter.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.SkillRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class GoalSkillsFilter implements  GoalFilter {
    private final SkillRepository skillRepository;

    @Override
    public boolean isApplicable(GoalFilterDto filter) {
        List<Skill> filterSkills = skillRepository.findAllById(filter.getSkillsToAchieve());
        return !filterSkills.isEmpty() && filterSkills.size() == filter.getSkillsToAchieve().size();
    }

    @Override
    public Stream<Goal> apply(Stream<Goal> goals, GoalFilterDto filter) {
        List<Skill> filterSkills = skillRepository.findAllById(filter.getSkillsToAchieve());
        Set<Skill> filterSkillsSet = new HashSet<>(filterSkills);
        return goals.filter(goal -> {
            Set<Skill> goalSkillsSet = new HashSet<>(goal.getSkillsToAchieve());
            return goalSkillsSet.equals(filterSkillsSet);
        });
    }
}
