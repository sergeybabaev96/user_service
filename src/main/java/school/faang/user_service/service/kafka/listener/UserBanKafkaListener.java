package school.faang.user_service.service.kafka.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.user.UserService;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserBanKafkaListener {
    private final UserService userService;

    @KafkaListener(topics = "user-ban-topic", groupId = "user-ban-group")
    public void listenUserBan(String userId) {
        log.info("Received request from kafka to ban user with ID {}", userId);
        userService.banUser(Long.valueOf(userId));
    }
}
