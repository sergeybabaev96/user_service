package school.faang.user_service.publisher;

import io.lettuce.core.RedisException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import school.faang.user_service.config.redis.RedisProperties;
import school.faang.user_service.event.MentorshipRequestEvent;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestedEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    private MentorshipRequestedEventPublisher publisher;

    private RedisProperties redisProperties;
    private MentorshipRequestEvent event;
    private String channel;

    @BeforeEach
    void setUp() {
        redisProperties = TestRedisPropertiesFactory.createDefaultRedisProperties();
        publisher = new MentorshipRequestedEventPublisher(redisTemplate, redisProperties);
        event = new MentorshipRequestEvent(1L, 2L, LocalDateTime.now());
        channel = redisProperties.channel().mentorshipRequest();
    }

    @Test
    void testPublishSuccess() {
        CompletableFuture<Void> result = publisher.publish(event);

        assertTrue(result.isDone());
        assertFalse(result.isCompletedExceptionally());
        verify(redisTemplate, times(1)).convertAndSend(channel, event);
    }

    @Test
    void testPublishRedisException() {
        doThrow(new RedisException("Redis error")).when(redisTemplate).convertAndSend(channel, event);

        CompletableFuture<Void> result = publisher.publish(event);

        assertTrue(result.isCompletedExceptionally());
        verify(redisTemplate, times(1)).convertAndSend(channel, event);
    }
}
