package school.faang.user_service.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.redis.event.RedisEvent;

@Component
@RequiredArgsConstructor
public class PremiumBoughtEventPublisher implements MessagePublisher {
    private final RedisTemplate<String, RedisEvent> redisTemplate;
    private final ChannelTopic topic;

    @Override
    public void publish(RedisEvent event) {
        redisTemplate.convertAndSend(topic.getTopic(), event);
    }
}
