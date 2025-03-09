package school.faang.user_service.kafka.event;

import lombok.Data;

@Data
public class GoalSetEvent {

    private final long userId;
    private final long goalId;
}
