package school.faang.user_service.kafka.goal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.enums.goal.GoalEventType;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoalCompletedEvent {

    private Long actorId;

    private Long goalId;

    private GoalEventType eventType;

    private LocalDateTime receivedAt;
}