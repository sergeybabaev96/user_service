package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.events.RedisEvent;

/**
 * Публикует события в аналитик сервис
 */
@Component
@RequiredArgsConstructor
public class RedisEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;

    public <T extends RedisEvent> void publish(T event) {
        String topic = event.getChanelEvent();
        redisTemplate.convertAndSend(topic, event);
    }
}