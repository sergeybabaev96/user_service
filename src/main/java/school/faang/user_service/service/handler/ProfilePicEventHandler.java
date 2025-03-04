package school.faang.user_service.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.ProfilePicEvent;
import school.faang.user_service.mapper.RedisEventMapper;
import school.faang.user_service.redis.MessagePublisher;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProfilePicEventHandler {
    private final MessagePublisher publisher;
    private final RedisEventMapper mapper;

    @Value("${user-service.redis.profile-pic-channel}")
    private String topic;

    @EventListener
    @Async
    public void handleProfilePicUpload(ProfilePicEvent event) {
        log.info("Handling profile pic upload event");
        publisher.publish(mapper.toRedisEvent(event), topic);
        log.info("Profile pic upload event sent to Redis");
    }
}
