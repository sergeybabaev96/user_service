package school.faang.user_service.rating.publisher;

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

    private final KafkaTemplate<String, FollowerEvent> kafkaTemplate;

    @Value("${spring.kafka.topics.follower}")
    private String topic;

    public void publish(FollowerEvent followerEvent) {
        log.info("Publishing follower event: {}", followerEvent);
        kafkaTemplate.send(topic, followerEvent);
    }
}
