package school.faang.user_service.publisher.mentorship;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.MentorshipStartEvent;
import school.faang.user_service.publisher.EventPublisher;

@Component
public class MentorshipEventPublisher extends EventPublisher<MentorshipStartEvent> {

    public MentorshipEventPublisher(
            @Value("${spring.data.redis.channel.mentorship}") String channelTopic,
            RedisTemplate<String, Object> redisTemplate
    ) {
        super(channelTopic, redisTemplate);
    }
}
