package school.faang.user_service.service.kafka.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaPublisher {
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendInTransaction(Object object, String topic) {
        kafkaTemplate.executeInTransaction(kafkaOperations -> {
            try {
                kafkaOperations.send(topic, objectMapper.writeValueAsString(object));
                log.info("Published to kafka: {}", object);
            } catch (JsonProcessingException e) {
                log.error("Serialization error", e);
                throw new RuntimeException(e);
            }
            return true;
        });
    }
}
