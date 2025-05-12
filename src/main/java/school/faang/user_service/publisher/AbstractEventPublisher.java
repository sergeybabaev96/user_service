package school.faang.user_service.publisher;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.dto.EventMessage;

@Slf4j
@AllArgsConstructor
public class AbstractEventPublisher implements EventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    protected ChannelTopic channelTopic;

    @Override
    public void publish(@Valid EventMessage message) {
        try {
            redisTemplate.convertAndSend(channelTopic.getTopic(), message);
        } catch (Exception e) {
            log.error("Failed publish event:{} to topic {}", message, channelTopic.getTopic(), e);
        }
    }
}
