package school.faang.user_service.publisher.subscription;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.redis.RedisProperties;
import school.faang.user_service.dto.event.SubscriptionEventDto;

@Component
@Slf4j
@RequiredArgsConstructor
public class SubscriptionPublisher {

    private final RedisProperties redisProperties;
    private final RedisTemplate<String, Object> redisTemplate;

    public void publishFollowEvent(SubscriptionEventDto event) {
        publishEvent(redisProperties.getChannel().getFollower(), event);
    }

    public void publishUnfollowEvent(SubscriptionEventDto event) {
        publishEvent(redisProperties.getChannel().getUnfollower(), event);
    }

    private void publishEvent(String channel, SubscriptionEventDto event) {
        redisTemplate.convertAndSend(channel, event);
        log.info("Event {} published to Redis channel '{}': {}", event.getClass(), channel, event);
    }
}
