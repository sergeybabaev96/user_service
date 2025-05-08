package school.faang.user_service.filter.goal;

import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public interface GoalFilter {
    boolean doFilter(Goal goal);

    static List<? extends GoalFilter> createFilters(GoalFilterDto dto) {
        return Stream.of(
                dto.getDescription() != null ? new GoalDescriptionFilter(dto.getDescription()) : null,
                dto.getTitle() != null ? new GoalTitleFilter(dto.getTitle()) : null,
                dto.getStatus() != null ? new GoalStatusFilter(dto.getStatus()) : null,
                dto.getSkillTitles() != null ? new GoalSkillsTitlesFilter(dto.getSkillTitles()) : null,
                dto.getCreatedBefore() != null ? new GoalCreateBeforeFilter(dto.getCreatedBefore()) : null,
                dto.getCreatedAfter() != null ? new GoalCreateAfterFilter(dto.getCreatedAfter()) : null,
                dto.getUpdatedBefore() != null ? new GoalUpdateBeforeFilter(dto.getUpdatedBefore()) : null,
                dto.getUpdatedAfter() != null ? new GoalUpdateAfterFilter(dto.getUpdatedAfter()) : null
        )
                .filter(Objects::nonNull)
                .toList();
    }
}
