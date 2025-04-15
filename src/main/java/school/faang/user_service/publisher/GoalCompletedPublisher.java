package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.analytics.GoalCompletedEvent;

@Component
@RequiredArgsConstructor
public class GoalCompletedPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.channel.goal-completing-channel}")
    private String goalCompletedTopic;

    public void publish(GoalCompletedEvent event) {
        redisTemplate.convertAndSend(goalCompletedTopic, event);
    }
}
