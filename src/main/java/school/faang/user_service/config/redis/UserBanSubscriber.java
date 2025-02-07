package school.faang.user_service.config.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.RedisMessageNotCorrectTypeException;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserBanSubscriber implements RedisSubscriber {

    private final UserService userService;

    @Override
    public void handleMessage(Object message) {
        try {
            List<Long> userIdsForBan = (List<Long>) message;
            log.info("UserBanSubscriber received the message: {}", userIdsForBan);
            userService.setBannedField(true, userIdsForBan);
        } catch (ClassCastException e) {
            log.error("Message", e);
            log.error("Expected List<Long> but received redis message like: {}", message);
            throw new RedisMessageNotCorrectTypeException("Not correct type for redis message, expected List<Long>");
        }
    }
}
