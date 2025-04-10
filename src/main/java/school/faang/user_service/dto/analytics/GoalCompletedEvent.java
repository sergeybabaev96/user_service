package school.faang.user_service.dto.analytics;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record GoalCompletedEvent(
        Long userId,
        Long goalId,
        LocalDateTime completedAt
) {
}
