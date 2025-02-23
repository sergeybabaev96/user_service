package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RequestMentorshipEvent;

@Service
public class MentorshipAcceptedEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic channelTopic;
    private final ObjectMapper objectMapper;

    public MentorshipAcceptedEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                            @Qualifier("mentorshipTopic") ChannelTopic channelTopic,
                                            ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.channelTopic = channelTopic;
        this.objectMapper = objectMapper;
    }

    public void publish(RequestMentorshipEvent event) {
        try {
            String jsonEvent = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(channelTopic.getTopic(), event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing the event", e);
        }
    }
}