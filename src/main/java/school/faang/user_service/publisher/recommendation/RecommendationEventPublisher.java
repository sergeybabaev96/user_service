package school.faang.user_service.publisher.recommendation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.redis.Channels;
import school.faang.user_service.dto.RecommendationEvent;
import school.faang.user_service.publisher.EventPublisherAbstract;

@Component
public class RecommendationEventPublisher extends EventPublisherAbstract<RecommendationEvent> {
    private final Channels channels;

    public RecommendationEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                        ObjectMapper objectMapper,
                                        Channels channels) {
        super(redisTemplate, objectMapper);
        this.channels = channels;
    }

    @Override
    public void publish(RecommendationEvent event) {
        handleEvent(event, channels.getRecommendationChannel());
    }
}