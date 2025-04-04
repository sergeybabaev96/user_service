package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalCompletedEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoalCompletedEventPublisher {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${redis.topic.goal-completed}")
    private String goalCompletedTopic;

    @Async("eventPublisherThreadPool")
    public void publish(GoalCompletedEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(goalCompletedTopic, json);
            log.info("Published GoalCompletedEvent to topic '{}': {}", goalCompletedTopic, json);
        } catch (JsonProcessingException e) {
            log.error("Failed to publish GoalCompletedEvent", e);
        }
    }
}