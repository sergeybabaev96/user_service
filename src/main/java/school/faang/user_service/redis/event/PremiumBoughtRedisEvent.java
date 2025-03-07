package school.faang.user_service.redis.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PremiumBoughtRedisEvent implements RedisEvent {
    private String type;
    private Map<String, Object> data;
}
