package school.faang.user_service.publisher.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.kafka.KafkaTopicConfig;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaHeatFeedCacheProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicConfig kafkaTopicConfig;

    public void send(List<Long> message) {
        kafkaTemplate.send(kafkaTopicConfig.heatFeedCacheTopic().name(), message);
        log.info("Sent event to heat feed cache for {} users", message.size());
    }
}
