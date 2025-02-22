package school.faang.user_service.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.GoalCompletedEvent;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalCompletedEventPublisher {

    private static final String GOAL_COMPLETED_TOPIC = "goal_completed";
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publish(GoalCompletedEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(GOAL_COMPLETED_TOPIC, message);
            log.info("Published GoalCompletedEvent: {}", message);
        } catch (JsonProcessingException e) {
            log.error("Error serializing GoalCompletedEvent: {}", e.getMessage());
            throw new RuntimeException("Error publishing GoalCompletedEvent", e);
        }
    }
}
