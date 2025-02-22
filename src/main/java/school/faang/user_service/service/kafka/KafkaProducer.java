package school.faang.user_service.service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.kafka.UserProfileViewedDto;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaProducer {
    private final KafkaTemplate<String, Object> template;

    @Value("${user-profile-viewed.topic-name}")
    String userProfileViewedTopicName;

    public void sendMessage(Long viewerId, Long profileOwnerId) {
        String uniqueKey = UUID.randomUUID().toString();
        UserProfileViewedDto dto = new UserProfileViewedDto(viewerId, profileOwnerId, LocalDateTime.now());
        template.send(userProfileViewedTopicName, uniqueKey, dto);
    }
}
