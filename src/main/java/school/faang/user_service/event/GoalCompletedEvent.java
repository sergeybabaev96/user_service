package school.faang.user_service.event;

import java.time.LocalDateTime;

public record GoalCompletedEvent(
        long userId,
        long goalId,
        LocalDateTime completedAt
) {
}
