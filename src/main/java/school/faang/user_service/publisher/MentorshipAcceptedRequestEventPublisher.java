package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipAcceptedEvent;

@Component
@RequiredArgsConstructor
public class MentorshipAcceptedRequestEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.channel.mentorship-accepted-event}")
    private String topic;

    public void publish(MentorshipAcceptedEvent event) {
        redisTemplate.convertAndSend(topic, event);
    }


}
