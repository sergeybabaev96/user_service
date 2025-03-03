package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.ProfileViewEventDto;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class ProfileViewEventPublisher {
    private final KafkaTemplate<String, Object> template;
    @Value("${user-profile-viewed.topic-name}")
    String userProfileViewedTopicName;

    public void sendMessage(Long viewerId, Long profileOwnerId) {
        String uniqueKey = UUID.randomUUID().toString();
        ProfileViewEventDto dto = ProfileViewEventDto.builder()
                .viewerId(viewerId)
                .profileOwnerId(profileOwnerId)
                .viewedAt(LocalDateTime.now())
                .build();
        template.send(userProfileViewedTopicName, uniqueKey, dto);
    }
}
