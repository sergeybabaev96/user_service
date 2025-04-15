package school.faang.user_service.publisher;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import school.faang.user_service.dto.events.RedisEvent;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RedisEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    private RedisEventPublisher redisEventPublisher;

    static class TestEvent implements RedisEvent {
        private final String channel;

        public TestEvent(String channel) {
            this.channel = channel;
        }

        @Override
        public String getChanelEvent() {
            return channel;
        }
    }

    @Test
    public void givenValidData_whenPublish_thenSuccessPublish() {
        TestEvent event = new TestEvent("test-channel");

        redisEventPublisher.publish(event);

        verify(redisTemplate).convertAndSend("test-channel", event);

    }
}
