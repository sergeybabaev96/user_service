package school.faang.user_service.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.FollowerEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class FollowerEventPublisher {

    @Value("${spring.kafka.topics.follower}")
    private String topic;

    private final KafkaTemplate<String, FollowerEvent> kafkaTemplate;

    public void publish(FollowerEvent followerEvent) {
        kafkaTemplate.send(topic, followerEvent);
        log.info("Publishing follower event: {}", followerEvent);
    }
}
