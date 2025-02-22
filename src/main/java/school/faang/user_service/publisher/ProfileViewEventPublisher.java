package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.event.ProfileViewEvent;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProfileViewEventPublisher {
    @Value("${spring.channels.profile-view}")
    private String profileViewChannel;

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserContext userContext;

    public void publisherEvent(long userId) {
        var viewerId = userContext.getUserId();
        var event = ProfileViewEvent.builder()
                .receiverId(userId)
                .actorId(viewerId)
                .receivedAt(LocalDateTime.now())
                .build();
        redisTemplate.convertAndSend(profileViewChannel, event);
    }
}
