package school.faang.user_service.listener.user_ban;

import faang.school.event.UserBanEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import school.faang.user_service.listener.EventListener;
import school.faang.user_service.service.user.UserService;

@RequiredArgsConstructor
@Component
public class UserBanEventListener implements EventListener<UserBanEvent> {

    private final UserService userService;

    @Override
    @KafkaListener(topics = "${spring.kafka.topics.user-ban-topic.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenEvent(UserBanEvent event) {
        userService.setBannedField(event.getUserId(), event.isBanned());
    }
}
