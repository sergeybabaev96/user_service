package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

@RequiredArgsConstructor
public abstract class EventPublisher<T> {

    private final String channelTopic;

    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(T event) {
        redisTemplate.convertAndSend(channelTopic, event);
    }
}
