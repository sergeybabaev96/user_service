package school.faang.user_service.publisher.profileview;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.ProfileViewEvent;
import school.faang.user_service.publisher.AbstractEventPublisher;

@Component
public class ProfileViewEventPublisher extends AbstractEventPublisher<ProfileViewEvent> {
    public ProfileViewEventPublisher(
            @Value("${spring.data.redis.channels.profile-view}") String channelTopic,
            RedisTemplate<String, Object> redisTemplate
    ) {
        super(channelTopic, redisTemplate);
    }
}
