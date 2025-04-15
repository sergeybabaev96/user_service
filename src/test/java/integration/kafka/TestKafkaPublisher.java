package integration.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import school.faang.user_service.utils.JsonUtils;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
@ActiveProfiles("test")
public class TestKafkaPublisher {

    @Autowired
    private JsonUtils jsonUtils;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendInTransaction(Object object, String topic, String correlationHeader, String correlationId) {
        kafkaTemplate.executeInTransaction(kafkaOperations -> {
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, jsonUtils.serialize(object));
            if (correlationId != null) {
                record.headers().add(new RecordHeader(correlationHeader, correlationId.getBytes(StandardCharsets.UTF_8)));
            }
            kafkaOperations.send(record);
            log.info("Published to Kafka: {} with correlationId: {}", object, correlationId);
            return true;
        });
    }
}
