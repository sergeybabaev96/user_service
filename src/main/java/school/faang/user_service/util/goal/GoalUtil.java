package school.faang.user_service.util.goal;

import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;
import java.util.List;

public class GoalUtil {

    public static void updateTime(Goal goalToUpdate, LocalDateTime time) {
        goalToUpdate.setUpdatedAt(time);
    }

    public static boolean goalFilter(Goal goal, GoalFilterDto filter) {
        String description = filter.getDescription();
        if (description != null && !goal.getDescription().contains(description)) return false;

        String title = filter.getTitle();
        if (title != null && !goal.getTitle().equals(title)) return false;

        GoalStatus status = filter.getStatus();
        if (status != null && status != goal.getStatus()) return false;

        List<String> skillTitles = filter.getSkillTitles();
        if (skillTitles != null && !goal.getSkillsToAchieve().stream()
                .map(Skill::getTitle)
                .toList()
                .containsAll(skillTitles)) return false;

        LocalDateTime createdBefore = filter.getCreatedBefore();
        if (createdBefore != null && goal.getCreatedAt().isAfter(createdBefore)) return false;

        LocalDateTime createdAfter = filter.getCreatedAfter();
        if (createdAfter != null && goal.getCreatedAt().isBefore(createdAfter)) return false;

        LocalDateTime updatedBefore = filter.getUpdatedBefore();
        if (updatedBefore != null && goal.getUpdatedAt().isAfter(updatedBefore)) return false;

        LocalDateTime updatedAfter = filter.getUpdatedAfter();
        if (updatedAfter != null && goal.getUpdatedAt().isBefore(updatedAfter)) return false;

        return true;
    }
}
