package school.faang.user_service.filter.goal;

import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

public record GoalSkillsTitlesFilter(List<String> skillTitles) implements GoalFilter {
    @Override
    public boolean doFilter(Goal goal) {
        return goal.getSkillsToAchieve().stream()
                .map(Skill::getTitle)
                .toList()
                .containsAll(skillTitles);
    }
}
