package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.UserProfile;
import school.faang.user_service.event.AnalyticsProfileViewEvent;
import school.faang.user_service.exception.EventSerializationException;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;

@Slf4j
@Component
public class AnalyticsProfileViewPublisher implements EventPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final UserContext userContext;

    @Value("${spring.kafka.topics.analytics-user-profile-view-topic.name}")
    private String analyticsProfileViewTopic;

    public AnalyticsProfileViewPublisher(KafkaTemplate<String, String> kafkaTemplate, UserService userService, UserContext userContext) {
        this.kafkaTemplate = kafkaTemplate;
        this.userService = userService;
        this.userContext = userContext;
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
    }

    @Override
    public void publishEvent(Object dto) {
        Long userId = ((UserProfile) dto).getUserId();
        Long viewerUserId = userContext.getUserId();

        if (userService.userExists(viewerUserId) && !userId.equals(viewerUserId)) {
            try {
                AnalyticsProfileViewEvent analyticsProfileViewEvent = AnalyticsProfileViewEvent.builder()
                        .userId(userId)
                        .viewerUserId(viewerUserId)
                        .timestamp(LocalDateTime.now())
                        .build();
                String json = objectMapper.writeValueAsString(analyticsProfileViewEvent);
                kafkaTemplate.send(analyticsProfileViewTopic, json);
            } catch (JsonProcessingException e) {
                log.error("Error parsing event: {}", dto, e);
                throw new EventSerializationException(e.getMessage());
            }
        }
    }
}
