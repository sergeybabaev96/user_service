package school.faang.user_service.publisher.subscription;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.redis.SubscriptionRedisProperties;
import school.faang.user_service.dto.event.SubscriptionEventDto;

@Component
@Slf4j
@RequiredArgsConstructor
public class SubscriptionPublisher {

    private final SubscriptionRedisProperties subscriptionRedisProperties;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RetryTemplate redisRetryTemplate;

    public void publishFollowEvent(SubscriptionEventDto event) {
        redisRetryTemplate.execute(context -> {
            publishEvent(subscriptionRedisProperties.getChannel().getFollower(), event);
            return null;
        });
    }

    public void publishUnfollowEvent(SubscriptionEventDto event) {
        redisRetryTemplate.execute(context -> {
            publishEvent(subscriptionRedisProperties.getChannel().getUnfollower(), event);
            return null;
        });
    }

    private void publishEvent(String channel, SubscriptionEventDto event) {
        try {
            redisTemplate.convertAndSend(channel, event);
            log.info("Event {} published to Redis channel '{}': {}", event.getClass(), channel, event);
        } catch (Exception e) {
            log.error("Failed to publish event to Redis channel '{}': {}", channel, event, e);
        }
    }
}
