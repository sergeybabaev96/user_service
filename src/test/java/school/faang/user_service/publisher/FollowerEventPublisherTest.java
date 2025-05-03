package school.faang.user_service.publisher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.dto.FollowerEvent;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FollowerEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ChannelTopic channelTopic;

    private FollowerEventPublisher publisher;

    private static final String TEST_TOPIC = "follower_event_topic";
    FollowerEvent event;

    @BeforeEach
    void setUp() {
        event = new FollowerEvent(1L,2L);
        when(channelTopic.getTopic()).thenReturn(TEST_TOPIC);
        publisher = new FollowerEventPublisher(redisTemplate, channelTopic);
    }

    @Test
    void testPublishSuccessful() {
        publisher.publish(event);
        verify(redisTemplate, times(1)).convertAndSend(TEST_TOPIC, event);
    }

    @Test
    void testPublishFail() {
        doThrow(new RuntimeException("Test Exception"))
                .when(redisTemplate)
                .convertAndSend(TEST_TOPIC, event);

        assertDoesNotThrow(() -> publisher.publish(event));
        verify(redisTemplate, times(1)).convertAndSend(TEST_TOPIC, event);
    }
}
