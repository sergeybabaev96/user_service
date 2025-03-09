package school.faang.user_service.subscriber.userban;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.Message;
import school.faang.user_service.dto.event.EventUsersBan;
import school.faang.user_service.exception.MessageMappingException;
import school.faang.user_service.service.event.EventService;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsersBanEventListenerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private EventService eventService;

    @Mock
    private Message message;

    @InjectMocks
    private UsersBanEventListener usersBanEventListener;

    private final byte[] pattern = "pattern".getBytes();

    private byte[] jsonMessageBytes;

    @BeforeEach
    void setUp() {
        String jsonMessage = "{\"userIdsToBan\":[1, 2, 3]}";
        jsonMessageBytes = jsonMessage.getBytes();
    }

    @Test
    void testOnMessageSuccess() throws IOException {
        EventUsersBan eventUsersBan = new EventUsersBan(Collections.singletonList(1L));

        when(message.getBody()).thenReturn(jsonMessageBytes);
        when(objectMapper.readValue(jsonMessageBytes, EventUsersBan.class)).thenReturn(eventUsersBan);

        usersBanEventListener.onMessage(message, pattern);

        verify(eventService, times(1)).banUsers(eventUsersBan.userIdsToBan());
    }

    @Test
    void testOnMessageIoException() throws IOException {

        when(message.getBody()).thenReturn(jsonMessageBytes);
        when(objectMapper.readValue(jsonMessageBytes, EventUsersBan.class))
                .thenThrow(new IOException("Failed to parse"));

        assertThrows(MessageMappingException.class, () -> {
            usersBanEventListener.onMessage(message, pattern);
        });

        verify(eventService, never()).banUsers(any());
    }
}