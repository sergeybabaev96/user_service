package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.FollowerEvent;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FollowerEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private FollowerEventPublisher publisher;

    private static final String TEST_TOPIC = "follower_event_topic";
    FollowerEvent event;

    @BeforeEach
    void setUp() {
        event = new FollowerEvent(1L,2L);
        ReflectionTestUtils.setField(publisher, "followerTopic", TEST_TOPIC);
    }

    @Test
    void testPublishSuccessful() throws JsonProcessingException {

        String json = "{followerId:1,followeeId:2}";
        when(objectMapper.writeValueAsString(event)).thenReturn(json);

        publisher.publish(event);

        verify(objectMapper, times(1)).writeValueAsString(event);
        verify(redisTemplate, times(1)).convertAndSend(TEST_TOPIC, json);
    }

    @Test
    void testPublishFail() throws JsonProcessingException {

        when(objectMapper.writeValueAsString(event)).thenThrow(new JsonProcessingException("Test Exception") {});

        publisher.publish(event);

        verify(objectMapper, times(1)).writeValueAsString(event);
        verify(redisTemplate, never()).convertAndSend(anyString(), any());
    }
}
