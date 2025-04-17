package school.faang.user_service.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.UserService;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisUserBanTopicListener implements MessageListener {
    private final UserService userService;

    @Override
    public void onMessage(@NotNull Message message, byte[] pattern) {
        String channelName = null;
        String payload = null;

        try {
            channelName = new String(message.getChannel());
            payload = new String(message.getBody());

            var userIdToBan = Long.parseLong(payload);

            userService.banUserById(userIdToBan);
            log.info("User with id {} is banned", userIdToBan);
        } catch (NumberFormatException ex) {
            log.error("Cannot convert value '{}' from channel {} to Long: {}",
                    payload, channelName, ex.getMessage(), ex);
        } catch (RuntimeException ex) {
            log.error("Processing message from channel user-ban is failed: {}", ex.getMessage(), ex);
        }
    }
}
