package school.faang.user_service.redis;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.redis.event.RedisEvent;

@Component
public class PremiumBoughtEventPublisher implements MessagePublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topic;

    public PremiumBoughtEventPublisher(RedisTemplate<String, Object> redisTemplate, @Qualifier("bought-premium-topic") ChannelTopic topic) {
        this.redisTemplate = redisTemplate;
        this.topic = topic;
    }

    @Override
    public void publish(RedisEvent event) {
        redisTemplate.convertAndSend(topic.getTopic(), event);
    }
}
