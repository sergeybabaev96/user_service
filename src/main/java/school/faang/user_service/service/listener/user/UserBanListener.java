package school.faang.user_service.service.listener.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.stereotype.Service;
import school.faang.user_service.properties.UserBanProperties;
import school.faang.user_service.service.listener.RedisMessageSubscriber;
import school.faang.user_service.service.user.UserBanService;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserBanListener implements RedisMessageSubscriber {

    private final UserBanService userBanService;
    private final UserBanProperties userBanProperties;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String messageBody = new String(message.getBody());
        log.info("Received message = {} from channel = {}", messageBody, message.getChannel());
        userBanService.banUsers(messageBody);
    }

    @Override
    public String getTopic() {
        return userBanProperties.getChannel();
    }
}
