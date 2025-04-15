package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.pubsub.FollowerEvent;

@Component
@RequiredArgsConstructor
public class FollowerEventPublisher implements EventPublisher<FollowerEvent> {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.channel.follower}")
    private String topic;

    @Override
    public void publish(FollowerEvent event) {
        redisTemplate.convertAndSend(topic, event);
    }
}
