package school.faang.user_service.publisher.userprofileview;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.redis.Channels;
import school.faang.user_service.events.UserProfileViewEvent;
import school.faang.user_service.publisher.EventPublisherAbstract;

@Slf4j
@Component
public class UserProfileViewEventPublisher extends EventPublisherAbstract<UserProfileViewEvent> {

    private final Channels channels;

    public UserProfileViewEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                         ObjectMapper objectMapper,
                                         Channels channels) {
        super(redisTemplate, objectMapper);
        this.channels = channels;
    }

    @Override
    public void publish(UserProfileViewEvent event) {
        handleEvent(event, channels.getProfileView());
        log.info("Event {} was published to channel {}", event, channels.getProfileView());
    }
}
