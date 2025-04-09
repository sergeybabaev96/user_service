package school.faang.user_service.service.kafka.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@RequiredArgsConstructor
public class KafkaPublisher {
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendInTransaction(Object object, String topic) {
        kafkaTemplate.executeInTransaction(kafkaOperations -> {
            try {
                kafkaOperations.send(topic, objectMapper.writeValueAsString(object));
                log.info("{} sent to payment_service", object);
            } catch (JsonProcessingException e) {
                log.error("Error while serializing PremiumPaymentRequestDto", e);
            }
            return true;
        });
        log.info("Published to kafka: {}", object);
    }
}
