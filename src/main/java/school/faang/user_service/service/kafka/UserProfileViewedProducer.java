package school.faang.user_service.service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.kafka.topic.UserProfileViewedTopicConfig;
import school.faang.user_service.dto.kafka.UserProfileViewedDto;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserProfileViewedProducer {
    private final KafkaTemplate<String, Object> template;
    private final UserProfileViewedTopicConfig topicConfig;

    @Async
    public void sendMessage(Long viewerId, Long profileOwnerId) {
        String uniqueKey = UUID.randomUUID().toString();
        UserProfileViewedDto dto = new UserProfileViewedDto(viewerId, profileOwnerId);
        template.send(topicConfig.getTopicName(), uniqueKey, dto);
    }
}
