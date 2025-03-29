package school.faang.user_service.publisher.mentorship;

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
import school.faang.user_service.events.MentorshipAcceptedEvent;
import school.faang.user_service.publisher.MentorshipAcceptedEventPublisher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipAcceptedEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Channels channels;

    @InjectMocks
    private MentorshipAcceptedEventPublisher publisher;

    private MentorshipAcceptedEvent mentorshipAcceptedEvent;

    private String mentorshipAcceptedChannel;

    @BeforeEach
    void setUp() {
        mentorshipAcceptedEvent = new MentorshipAcceptedEvent(1L, 2L, 3L);
        mentorshipAcceptedChannel = "mentorship_accepted_channel";
        when(channels.getMentorshipAcceptedChannel()).thenReturn(mentorshipAcceptedChannel);
    }

    @Test
    void testPublishSuccess() throws JsonProcessingException {
        String eventJson = "{{\"requesterId\":1,\"requestId\":2,\"mentorId\":3}}";
        when(objectMapper.writeValueAsString(mentorshipAcceptedEvent)).thenReturn(eventJson);

        publisher.publish(mentorshipAcceptedEvent);

        verify(objectMapper, times(1)).writeValueAsString(mentorshipAcceptedEvent);
        verify(redisTemplate, times(1)).convertAndSend(mentorshipAcceptedChannel, eventJson);
    }

    @Test
    void testPublishJsonProcessingException() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(mentorshipAcceptedEvent))
                .thenThrow(new JsonProcessingException("JSON error") {
                });

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> publisher.publish(mentorshipAcceptedEvent));

        assertEquals("school.faang.user_service.publisher.mentorship.MentorshipAcceptedEventPublisherTest$1:"
                + " JSON error", exception.getMessage());

        verify(objectMapper, times(1)).writeValueAsString(mentorshipAcceptedEvent);
        verify(redisTemplate, never()).convertAndSend(anyString(), anyString());

    }
}
