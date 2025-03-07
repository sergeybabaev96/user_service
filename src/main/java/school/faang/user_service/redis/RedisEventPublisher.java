package school.faang.user_service.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import school.faang.user_service.redis.event.RedisEvent;

@Component
@RequiredArgsConstructor
public class RedisEventPublisher implements MessagePublisher {
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @Retryable(retryFor = QueryTimeoutException.class, backoff = @Backoff(delay = 2000, multiplier = 2))
    public void publish(RedisEvent event, String topic) {
        redisTemplate.convertAndSend(topic, event);
    }
}
