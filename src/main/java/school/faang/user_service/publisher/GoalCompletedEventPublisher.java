package school.faang.user_service.publisher;

import io.lettuce.core.RedisConnectionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.GoalCompletedEvent;
import school.faang.user_service.exception.RedisPublishingException;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoalCompletedEventPublisher implements EventPublisher<GoalCompletedEvent> {
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.channel.goal-completed}")
    private String topic;

    @Retryable(retryFor = {RedisConnectionException.class, RedisPublishingException.class},
            maxAttempts = 5, backoff = @Backoff(delay = 2000, multiplier = 2))
    public void publish(GoalCompletedEvent event) {
        try {
            redisTemplate.convertAndSend(topic, event);
        } catch (RedisConnectionException e) {
            log.error("Redis connection error while publishing event: {}", event, e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while publishing event: {}", event, e);
            throw new RedisPublishingException("Unexpected error while publishing event to Redis", e);
        }
    }

    @Override
    public Class<GoalCompletedEvent> getEventClass() {
        return GoalCompletedEvent.class;
    }
}
