package school.faang.user_service.queue.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecommendationEvent;
import school.faang.user_service.queue.RedisPublisher;

@Component
@RequiredArgsConstructor
public class RecommendationEventPublisherImpl implements RedisPublisher<RecommendationEvent> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic recommendationEventTopic;

    @Override
    @Retryable(retryFor = {RuntimeException.class}, backoff = @Backoff(delay = 3000))
    public void publish(final RecommendationEvent message) {
        redisTemplate.convertAndSend(recommendationEventTopic.getTopic(), message);
    }
}