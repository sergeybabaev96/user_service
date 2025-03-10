package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
@RequiredArgsConstructor
public abstract class EventPublisherAbstract<T> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public abstract void publish(T event);

    protected void handleEvent(T event, String topic) {
        try {
            String eventToPublish = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(topic, eventToPublish);
        } catch (JsonProcessingException ex) {
            log.error("An error occurred while working with JSON: ", ex);
            throw new RuntimeException(ex);
        }
    }
}
