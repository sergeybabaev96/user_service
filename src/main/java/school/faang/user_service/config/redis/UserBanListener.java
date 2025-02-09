package school.faang.user_service.config.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.UserService;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserBanListener {

    private final UserService userService;

    public void handleMessage(String userId) {
        log.info("Received ban event for user ID: {}", userId);
        userService.banUser(Long.parseLong(userId));
    }
}
