package school.faang.user_service.publisher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import school.faang.user_service.config.redis.RedisProperties;
import school.faang.user_service.event.SubscriptionEvent;

import java.time.LocalDateTime;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SubscriptionEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    private SubscriptionEventPublisher subscriptionEventPublisher;

    private RedisProperties redisProperties;
    private SubscriptionEvent event;
    private String subscriptionChannel;

    @BeforeEach
    void setUp() {
        redisProperties = TestRedisPropertiesFactory.createDefaultRedisProperties();
        event = new SubscriptionEvent(1L, 2L, LocalDateTime.now());
        subscriptionChannel = redisProperties.channel().subscriptionChannel();
        subscriptionEventPublisher = new SubscriptionEventPublisher(redisTemplate, null, redisProperties);
    }

    @Test
    void testPublish_success() {
        subscriptionEventPublisher.publish(event);

        verify(redisTemplate, times(1)).convertAndSend(subscriptionChannel, event);
    }
}