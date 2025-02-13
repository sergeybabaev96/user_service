package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoalCompletedEventPublisher {

    private final KafkaTemplate<String, GoalCompletedEvent> kafkaTemplate;

    @Value("${kafka.topic.goal-completed}")
    private String topic;

    public void publish(GoalCompletedEvent event) {
        log.info("Publishing event to topic {}: {}", topic, event);
        kafkaTemplate.send(topic, event);
    }
}