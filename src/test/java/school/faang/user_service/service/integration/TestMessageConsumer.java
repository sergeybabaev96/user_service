package school.faang.user_service.service.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.kafka.FollowUserEventDto;

@Slf4j
@Component
public class TestMessageConsumer {

    private FollowUserEventDto payload;

    public void receive(FollowUserEventDto message) {
        this.payload = message;
//        log.info("Success received");
    }

    public FollowUserEventDto getPayload() {
        return payload;
    }

    public void reset() {
        this.payload = null;
    }
}