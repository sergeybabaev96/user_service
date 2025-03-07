package school.faang.user_service.redis;

import school.faang.user_service.redis.event.RedisEvent;

public interface MessagePublisher {
    void publish(RedisEvent message, String topic);
}
