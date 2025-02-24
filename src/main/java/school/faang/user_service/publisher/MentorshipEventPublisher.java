package school.faang.user_service.publisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.messaging.MentorshipStartEvent;

@Slf4j
@Component
public class MentorshipEventPublisher extends MessagePublisher<MentorshipStartEvent> {

    public MentorshipEventPublisher(
            RedisTemplate<String, Object> redisTemplate,
            @Value("${spring.data.redis.channel.mentorship}") String channel
    ) {
        super(redisTemplate, channel);
    }
}
