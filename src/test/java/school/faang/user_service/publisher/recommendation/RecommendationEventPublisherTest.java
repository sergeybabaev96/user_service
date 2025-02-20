package school.faang.user_service.publisher.recommendation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import school.faang.user_service.config.redis.Channels;
import school.faang.user_service.dto.RecommendationEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendationEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Channels channels;

    @InjectMocks
    private RecommendationEventPublisher recommendationEventPublisher;

    private RecommendationEvent recommendationEvent;
    private String recommendationChannel;

    @BeforeEach
    void setUp() {
        recommendationEvent = new RecommendationEvent(1L, 2L, 3L);
        recommendationChannel = "recommendationChannel";
        when(channels.getRecommendationChannel()).thenReturn(recommendationChannel);
    }

    @Test
    void testPublish_Success() throws JsonProcessingException {
        String eventJson = "{\"requesterId\":1,\"receiverId\":2,\"recommendationId\":3}";
        when(objectMapper.writeValueAsString(recommendationEvent)).thenReturn(eventJson);

        recommendationEventPublisher.publish(recommendationEvent);

        verify(objectMapper, times(1)).writeValueAsString(recommendationEvent);
        verify(redisTemplate, times(1)).convertAndSend(recommendationChannel, eventJson);
    }

    @Test
    void testPublish_JsonProcessingException() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(recommendationEvent))
                .thenThrow(new JsonProcessingException("JSON error") {});

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> recommendationEventPublisher.publish(recommendationEvent));

        assertEquals("school.faang.user_service.publisher.recommendation.RecommendationEventPublisherTest$1:"
                + " JSON error", exception.getMessage());

        verify(objectMapper, times(1)).writeValueAsString(recommendationEvent);
        verify(redisTemplate, never()).convertAndSend(anyString(), anyString());
    }
}