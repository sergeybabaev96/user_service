package school.faang.user_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.ConvertingDataException;
import school.faang.user_service.service.UserService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthorBanListener implements MessageListener {

    private final ObjectMapper objectMapper;
    private final UserService userService;

    public void onMessage(Message message, @Nullable byte[] pattern) {
        try {
            log.debug("Received new message: {}", message.getBody());
            Long userId = objectMapper.readValue(message.getBody(), Long.class);
            userService.banUser(userId);
        } catch (IOException e) {
            throw new ConvertingDataException("Deserialized JSON %s into object %s failed",
                    message.getBody(), Long.class.getName());
        }
    }
}
