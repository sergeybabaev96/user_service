package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.SkillAcquiredEvent;

@Component
@RequiredArgsConstructor
public class SkillAcquiredEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.channel.skill-acquired}")
    private String skillAcquiredChannel;

    public void publish(SkillAcquiredEvent event) {
        redisTemplate.convertAndSend(skillAcquiredChannel, event);
    }
}
