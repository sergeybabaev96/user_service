package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.redis.Channels;
import school.faang.user_service.events.MentorshipAcceptedEvent;

@Component
public class MentorshipAcceptedEventPublisher extends EventPublisherAbstract<MentorshipAcceptedEvent> {

    private final Channels channels;

    public MentorshipAcceptedEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                           ObjectMapper objectMapper,
                                           Channels channels) {
        super(redisTemplate, objectMapper);
        this.channels = channels;
    }

    @Override
    public void publish(MentorshipAcceptedEvent event) {
        handleEvent(event, channels.getMentorshipAcceptedChannel());
    }
}
