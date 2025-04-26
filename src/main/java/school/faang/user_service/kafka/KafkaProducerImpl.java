package school.faang.user_service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.kafka.event.GoalSetEvent;
import school.faang.user_service.properties.UserServiceProperties;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaProducerImpl implements KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final UserServiceProperties userServiceProperties;
    private final ObjectMapper objectMapper;

    @Override
    public void produce(GoalSetEvent event) {
        try {
            String eventAsString = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(
                    userServiceProperties
                            .getKafka()
                            .getChannel()
                            .getGoalAchievementEvent(),
                    eventAsString
            );
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
