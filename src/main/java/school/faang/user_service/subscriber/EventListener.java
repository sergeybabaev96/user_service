package school.faang.user_service.subscriber;

import org.springframework.data.redis.connection.MessageListener;

public interface EventListener extends MessageListener {
    String getChannelName();
}
