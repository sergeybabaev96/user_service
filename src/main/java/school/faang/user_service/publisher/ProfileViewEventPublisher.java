package school.faang.user_service.publisher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.ProfileViewEvent;

@Service
public class ProfileViewEventPublisher extends AbstractEventPublisher<ProfileViewEvent> {
    public ProfileViewEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                     @Value("${spring.channels.profile-view}") String channel) {
        super(redisTemplate, channel);
    }

    @Override
    public void publisherEvent(ProfileViewEvent event) {
        super.publisherEvent(event);
    }
}
