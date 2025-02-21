package school.faang.user_service.messaging.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationRequestedEvent;

@Service
public class RecommendationRequestedEventPublisher {

    private final KafkaTemplate<String, RecommendationRequestedEvent> kafkaTemplate;

    public RecommendationRequestedEventPublisher(KafkaTemplate<String, RecommendationRequestedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishEvent(RecommendationRequestedEvent event) {
        kafkaTemplate.send("recommendation_requests", event);
    }
}