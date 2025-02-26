package school.faang.user_service.kafka.goal;

import java.time.LocalDateTime;

public record GoalCompletedEvent(Long userId, Long goalId, LocalDateTime date) {
}