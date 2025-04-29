package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class SkillAcquiredEventPublisher extends AbstractEventPublisher {

    public SkillAcquiredEventPublisher(
            RedisTemplate<String, Object> redisTemplate,
            ObjectMapper objectMapper,
            @Qualifier("skillChannel") ChannelTopic skillTopic
    ) {
        super(redisTemplate, objectMapper);
        channelTopic = skillTopic;
    }
}
