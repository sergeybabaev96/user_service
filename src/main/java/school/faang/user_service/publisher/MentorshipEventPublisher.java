package school.faang.user_service.publisher;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.messaging.MentorshipStartEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class MentorshipEventPublisher {

    @Value("${spring.data.redis.channel.mentorship}")
    private String mentorshipTopic;

    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(MentorshipStartEvent event) {
        redisTemplate.convertAndSend(mentorshipTopic, event);
    }
}
