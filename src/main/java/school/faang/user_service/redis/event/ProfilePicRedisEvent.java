package school.faang.user_service.redis.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfilePicRedisEvent implements RedisEvent {
    private Long userId;
    private String picKey;
}
