package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.RetryProperties;
import school.faang.user_service.config.redis.RedisProperties;
import school.faang.user_service.event.SubscriptionEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionEventPublisher implements EventPublisher<SubscriptionEvent> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RetryProperties retryProperties;
    private final RedisProperties redisProperties;

    @Override
    @Retryable(retryFor = Exception.class,
            maxAttemptsExpression = "#{@retryProperties.maxAttempts}",
            backoff = @Backoff(
                    delayExpression = "#{@retryProperties.initialDelay}",
                    multiplierExpression = "#{@retryProperties.multiplier}",
                    maxDelayExpression = "#{@retryProperties.maxDelay}"
            )
    )
    public void publish(SubscriptionEvent message) {
        redisTemplate.convertAndSend(redisProperties.channel().subscriptionChannel(), message);
        log.info("New Subscription event sent to channel id: {}", redisProperties.channel().subscriptionChannel());
    }

    @Override
    public Class<SubscriptionEvent> getEventClass() {
        return SubscriptionEvent.class;
    }
}
