package school.faang.user_service.publisher.profileview;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.ProfileViewEvent;
import school.faang.user_service.publisher.EventPublisher;

@Component
public class ProfileViewEventPublisher extends EventPublisher<ProfileViewEvent> {
    public ProfileViewEventPublisher(
            @Value("${spring.data.redis.channel.profile}") String channelTopic,
            RedisTemplate<String, Object> redisTemplate
    ) {
        super(channelTopic, redisTemplate);
    }
}
