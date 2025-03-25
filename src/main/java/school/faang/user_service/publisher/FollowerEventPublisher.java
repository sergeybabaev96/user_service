package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.subscription.FollowerEvent;

@Component
@RequiredArgsConstructor
public class FollowerEventPublisher {

    @Value("${topic.follower-topic}")
    private String followerTopic;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publish(FollowerEvent event) throws JsonProcessingException {
        String followerEventJson = objectMapper.writeValueAsString(event);
        kafkaTemplate.send(followerTopic, followerEventJson);
    }
}
