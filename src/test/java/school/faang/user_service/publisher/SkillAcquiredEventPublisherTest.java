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
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.dto.SkillAcquiredEvent;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillAcquiredEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ChannelTopic channelTopic;

    @InjectMocks
    private SkillAcquiredEventPublisher publisher;

    private static final String TEST_TOPIC = "skill_channel";
    SkillAcquiredEvent event;

    @BeforeEach
    void setUp() {
        event = new SkillAcquiredEvent(1L,2L, 1L);
    }

    @Test
    void testPublishSuccessful() throws Exception {
        String json = "{authorId:1,recipientId:2,skillId:1}";
        when(objectMapper.writeValueAsString(event)).thenReturn(json);
        when(channelTopic.getTopic()).thenReturn(TEST_TOPIC);

        publisher.publish(event);

        verify(objectMapper, times(1)).writeValueAsString(event);
        verify(redisTemplate, times(1)).convertAndSend(TEST_TOPIC, json);
    }

    @Test
    void testPublishFail() throws Exception {
        when(objectMapper.writeValueAsString(event)).thenThrow(new JsonProcessingException("Test Exception") {});

        publisher.publish(event);

        verify(objectMapper, times(1)).writeValueAsString(event);
        verify(redisTemplate, never()).convertAndSend(anyString(), any());
    }
}
