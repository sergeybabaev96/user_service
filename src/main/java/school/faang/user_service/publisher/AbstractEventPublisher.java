package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.dto.EventMessage;

@Slf4j
@RequiredArgsConstructor
@AllArgsConstructor
public class AbstractEventPublisher implements EventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    protected ChannelTopic channelTopic;

    @Override
    public void publish(EventMessage message) {
        try {
            String jsonEvent = objectMapper.writeValueAsString(message);
            redisTemplate.convertAndSend(channelTopic.getTopic(), jsonEvent);
        } catch (Exception e) {
            log.error("Failed publish event:{} to topic {}", message, channelTopic.getTopic(), e);
        }
    }
}
