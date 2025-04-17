package school.faang.user_service.listener;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.Message;
import school.faang.user_service.service.UserService;

import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RedisUserBanTopicListenerTest {
    public static final Long TEST_USER_ID = 123L;
    public static final String USER_BAN_CHANNEL_NAME = "user-ban";

    @Mock
    private UserService userService;

    @Mock
    private Message message;

    @InjectMocks
    private RedisUserBanTopicListener redisUserBanTopicListener;

    @Test
    public void shouldBanUserById_callBanUserById_whenValidMessageReceived() {
        when(message.getChannel()).thenReturn(USER_BAN_CHANNEL_NAME.getBytes());
        when(message.getBody()).thenReturn(TEST_USER_ID.toString().getBytes());
        redisUserBanTopicListener.onMessage(message, null);

        verify(userService).banUserById(TEST_USER_ID);
    }

    @Test
    public void shouldBanUserById_doNotThrow_whenInvalidUserIdIsReceived() {
        when(message.getChannel()).thenReturn(USER_BAN_CHANNEL_NAME.getBytes());
        when(message.getBody()).thenReturn("not-a-number".getBytes());
        redisUserBanTopicListener.onMessage(message, null);

        verify(userService, never()).banUserById(anyLong());
    }

    @Test
    public void shouldBanUserById_doNotThrow_whenRuntimeExceptionIsThrownByUserService() {
        when(message.getChannel()).thenReturn(USER_BAN_CHANNEL_NAME.getBytes());
        when(message.getBody()).thenReturn(TEST_USER_ID.toString().getBytes());
        doThrow(new RuntimeException("Service error")).when(userService).banUserById(TEST_USER_ID);
        redisUserBanTopicListener.onMessage(message, null);

        verify(userService).banUserById(TEST_USER_ID);
    }

    @Test
    public void shouldBanUserById_doNotThrow_whenMessageBodyIsNull() {
        when(message.getChannel()).thenReturn(USER_BAN_CHANNEL_NAME.getBytes());
        when(message.getBody()).thenReturn(null);
        redisUserBanTopicListener.onMessage(message, null);

        verify(userService, never()).banUserById(anyLong());
    }
}