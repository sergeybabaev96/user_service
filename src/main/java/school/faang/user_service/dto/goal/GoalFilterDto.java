package school.faang.user_service.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalFilterDto {

    private String description;
    private String title;
    private GoalStatus status;
    private List<String> skillTitles;
    //todo goal_created_before and after

    public boolean doFilter(Goal goal) {
        if (description != null && !goal.getDescription().contains(description)) return false;
        if (title != null && !goal.getTitle().equals(title)) return false;
        if (status != null && status != goal.getStatus()) return false;
        if (skillTitles != null && !goal.getSkillsToAchieve().stream()
                .map(Skill::getTitle)
                .toList()
                .containsAll(skillTitles)) return false;
        return true;
    }
}
