package school.faang.user_service.messaging.publisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.messaging.event.EventStartEvent;

@Component
public class EventStartEventPublisherImpl implements EventStartEventPublisher {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ChannelTopic topic;

    @Override
    public void publish(EventStartEvent event) {
        redisTemplate.convertAndSend(topic.getTopic(), event);
    }
}
