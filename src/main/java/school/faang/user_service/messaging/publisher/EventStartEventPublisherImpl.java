package school.faang.user_service.messaging.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import school.faang.user_service.messaging.event.EventStartEvent;

@Service
@RequiredArgsConstructor
public class EventStartEventPublisherImpl implements EventStartEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topic;

    @Override
    public void publish(EventStartEvent event) {
        redisTemplate.convertAndSend(topic.getTopic(), event);
    }
}

