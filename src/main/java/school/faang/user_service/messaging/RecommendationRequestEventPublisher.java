package school.faang.user_service.messaging;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationRequestedEvent;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationRequestEventPublisher {

    @Value("${spring.kafka.topics.recommendation-requested}")
    private String recommendationRequestedTopic;

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;


    public void publish(RecommendationRequestedEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(recommendationRequestedTopic, message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
