package school.faang.user_service.messaging.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationRequestedEvent;
@Service
public class RecommendationRequestedEventPublisher {

    private final KafkaTemplate<String, RecommendationRequestedEvent> kafkaTemplate;
    private final String topic;

    public RecommendationRequestedEventPublisher(KafkaTemplate<String, RecommendationRequestedEvent> kafkaTemplate,
                                                 @Value("${spring.kafka.topics.recommendation_requests}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publishEvent(RecommendationRequestedEvent event) {
        kafkaTemplate.send(topic, event);
    }
}