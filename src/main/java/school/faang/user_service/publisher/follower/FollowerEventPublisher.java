package school.faang.user_service.publisher.follower;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.redis.Channels;
import school.faang.user_service.dto.FollowerEvent;
import school.faang.user_service.publisher.EventPublisherAbstract;

@Slf4j
@Component

public class FollowerEventPublisher extends EventPublisherAbstract<FollowerEvent> {

    private final Channels channels;
    public FollowerEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                        ObjectMapper objectMapper,
                                        Channels channels) {
        super(redisTemplate, objectMapper);
        this.channels = channels;
    }

    @Override
    public void publish(FollowerEvent event) {
        handleEvent(event, channels.getFollowerChannel());
        log.info("Event {} was published to channel {}", event, channels.getFollowerChannel());
    }
}
