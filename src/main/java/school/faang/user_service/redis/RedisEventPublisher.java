package school.faang.user_service.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.redis.event.RedisEvent;

@Component
@RequiredArgsConstructor
public class RedisEventPublisher implements MessagePublisher {
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void publish(RedisEvent event, String topic) {
        redisTemplate.convertAndSend(topic, event);
    }
}
