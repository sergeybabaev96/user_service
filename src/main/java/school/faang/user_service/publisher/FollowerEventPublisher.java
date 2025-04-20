package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.FollowerEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class FollowerEventPublisher {

    @Value("${spring.data.redis.channel.follower-topic}")
    private String followerTopic;

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public void publish(FollowerEvent followerEvent) {
        try {
            String jsonEvent = objectMapper.writeValueAsString(followerEvent);
            redisTemplate.convertAndSend(followerTopic, jsonEvent);
        } catch (Exception e) {
            log.error("Failed publish event to topic {}", followerTopic, e);
        }
    }

}
