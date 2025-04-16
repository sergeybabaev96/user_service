package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.MentorshipAcceptedEventDto;
import school.faang.user_service.properties.RedisProperties;

@Component
@RequiredArgsConstructor
public class MentorshipAcceptedEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisProperties redisProperties;

    public void publish(MentorshipAcceptedEventDto event) {
        String topic = redisProperties.getTopic(event.getEventType());
        redisTemplate.convertAndSend(topic,event);
    }
}
