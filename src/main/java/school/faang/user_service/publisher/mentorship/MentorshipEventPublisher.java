package school.faang.user_service.publisher.mentorship;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.MentorshipStartEvent;
import school.faang.user_service.publisher.AbstractEventPublisher;

@Component
public class MentorshipEventPublisher extends AbstractEventPublisher<MentorshipStartEvent> {

    public MentorshipEventPublisher(
            @Value("${spring.data.redis.channels.mentorship}") String channel,
            RedisTemplate<String, Object> redisTemplate
    ) {
        super(channel, redisTemplate);
    }
}
