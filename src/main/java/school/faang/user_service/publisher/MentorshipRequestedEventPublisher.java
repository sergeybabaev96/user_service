package school.faang.user_service.publisher;

import io.lettuce.core.RedisException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.redis.RedisProperties;
import school.faang.user_service.event.MentorshipRequestEvent;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class MentorshipRequestedEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisProperties redisProperties;

    @Retryable(
            value = {RedisException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 3000, multiplier = 2)
    )
    @Async("taskExecutor")
    public CompletableFuture<Void> publish(MentorshipRequestEvent event) {
        try {
            redisTemplate.convertAndSend(redisProperties.channel().mentorshipRequest(), event);
            log.info("Successfully published mentorship request event from user with id {} to user with id {}.",
                    event.getActorId(), event.getReceiverId());
            return CompletableFuture.completedFuture(null);
        } catch (RedisException e) {
            log.error("Redis error while publishing event: {}", e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }
}