package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;


@RequiredArgsConstructor
public abstract class KafkaEventPublisher<T> implements MessagePublisher<T> {
    private final NewTopic topic1;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(String msg) {
        kafkaTemplate.send(topic1.name(), msg);
    }
}
