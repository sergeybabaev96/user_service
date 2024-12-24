package school.faang.user_service.publisher;

import io.lettuce.core.RedisConnectionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.event.GoalCompletedEvent;
import school.faang.user_service.exception.RedisPublishingException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GoalCompletedEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    private GoalCompletedEventPublisher goalCompletedEventPublisher;

    private final String topic = "test-topic";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(goalCompletedEventPublisher, "topic", topic);
    }

    @Test
    void testPublishSuccess() {
        when(redisTemplate.convertAndSend(topic, setUpGoalCompletedEvent())).thenReturn(1L);

        goalCompletedEventPublisher.publish(setUpGoalCompletedEvent());

        verify(redisTemplate, times(1)).convertAndSend(topic, setUpGoalCompletedEvent());
    }

    @Test
    void testPublishRedisConnectionException() {
        when(redisTemplate.convertAndSend(topic, setUpGoalCompletedEvent())).thenThrow(RedisConnectionException.class);

        assertThrows(RedisConnectionException.class, () -> goalCompletedEventPublisher.publish(setUpGoalCompletedEvent()));

        verify(redisTemplate, times(1)).convertAndSend(topic, setUpGoalCompletedEvent());
    }

    @Test
    void testPublishUnexpectedException() {
        when(redisTemplate.convertAndSend(topic, setUpGoalCompletedEvent()))
                .thenThrow(new RuntimeException("Unexpected error"));

        RedisPublishingException exception = assertThrows(RedisPublishingException.class, () ->
                goalCompletedEventPublisher.publish(setUpGoalCompletedEvent()));

        verify(redisTemplate, times(1)).convertAndSend(topic, setUpGoalCompletedEvent());
        assertEquals("Unexpected error while publishing event to Redis", exception.getMessage());
    }

    private GoalCompletedEvent setUpGoalCompletedEvent() {
        return new GoalCompletedEvent(1L, 2L,
                LocalDateTime.of(2021, 1, 1, 0, 0));
    }
}
