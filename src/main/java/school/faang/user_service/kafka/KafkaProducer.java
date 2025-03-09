package school.faang.user_service.kafka;

import school.faang.user_service.kafka.event.GoalSetEvent;

public interface KafkaProducer {

    void produceToAchievementService(GoalSetEvent event);
}
