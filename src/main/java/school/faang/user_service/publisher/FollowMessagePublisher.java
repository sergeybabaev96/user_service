package school.faang.user_service.publisher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.FollowEvent;

@Component
public class FollowMessagePublisher extends MessagePublisher<FollowEvent> {
    public FollowMessagePublisher(
            RedisTemplate<String, Object> redisTemplate,
            @Value("${spring.data.redis.channel.follow}") String channel
    ) {
        super(redisTemplate, channel);
    }
}
