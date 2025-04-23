package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.pubsub.ProfileViewEvent;
@Component
@RequiredArgsConstructor
public class ProfileViewEventPublisher implements EventPublisher<ProfileViewEvent> {

    @Value("${spring.data.redis.channel.profile-view}")
    private String channel;

    private final RedisTemplate<String, Object> redisTemplate;
    @Override
    public void publish(ProfileViewEvent event) {
        redisTemplate.convertAndSend(channel, event);
    }
}
