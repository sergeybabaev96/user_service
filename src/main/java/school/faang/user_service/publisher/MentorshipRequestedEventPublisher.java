package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRequestedEvent;

@Component
@RequiredArgsConstructor
public class MentorshipRequestedEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.channel.mentorship-request}")
    private String mentorshipRequestChannel;

    public void publish(MentorshipRequestedEvent mentorshipRequestedEvent) {
        redisTemplate.convertAndSend(mentorshipRequestChannel, mentorshipRequestedEvent);
    }

}
