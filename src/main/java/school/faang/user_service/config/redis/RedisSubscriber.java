package school.faang.user_service.config.redis;

public interface RedisSubscriber {
    void handleMessage(Object message);
}
