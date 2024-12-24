package school.faang.user_service.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.UserService;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserBanListener implements MessageListener {
    private final UserService userService;

    public void onMessage(Message message, byte[] pattern) {
        try {
            String body = new String(message.getBody(), StandardCharsets.UTF_8);
            log.info("Received message: {}", body);
            Long userId = Long.parseLong(body);
            log.info("Banning user with ID: {}", userId);

            userService.banUser(userId);
        } catch (NumberFormatException e) {
            log.error("Invalid user ID received: {}", new String(message.getBody(), StandardCharsets.UTF_8), e);
        } catch (Exception e) {
            log.error("Error processing message", e);
        }
    }
}