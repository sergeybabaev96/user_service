package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

@RequiredArgsConstructor
public abstract class AbstractEventPublisher<T> {
    private final String channel;
    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(T event) {
        redisTemplate.convertAndSend(channel, event);
    }
}
