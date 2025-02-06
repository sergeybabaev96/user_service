package school.faang.user_service.service.listener;

import org.springframework.data.redis.connection.MessageListener;

public interface RedisMessageSubscriber extends MessageListener {

    String getTopic();
}
