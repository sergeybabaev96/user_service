package school.faang.user_service.kafka.goal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoalCompletedEventKafkaProducer implements KafkaProducer<GoalCompletedEvent> {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.goal.completed.topic}")
    private String topic;

    public void produce(GoalCompletedEvent event) throws JsonProcessingException {
        log.info("Publishing event to topic {}: {}", topic, event);
        kafkaTemplate.send(topic, objectMapper.writeValueAsString(event));
    }
}