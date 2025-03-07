package school.faang.user_service.service.integration;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.kafka.FollowUserEventDto;

@Component
public class TestMessageConsumer {

    private FollowUserEventDto payload;

    @KafkaListener(topics = "user-follows-user", groupId = "test-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void receive(FollowUserEventDto message) {
        this.payload = message;
    }

    public FollowUserEventDto getPayload() {
        return payload;
    }

    public void reset() {
        this.payload = null;
    }
}