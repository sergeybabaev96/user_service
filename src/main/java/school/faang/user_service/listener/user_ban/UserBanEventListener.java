package school.faang.user_service.listener.user_ban;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import school.faang.user_service.events.UserBanEvent;
import school.faang.user_service.listener.EventListener;
import school.faang.user_service.service.user.UserService;

@RequiredArgsConstructor
@Component
public class UserBanEventListener implements EventListener {

    private final UserService userService;
    private final ObjectMapper objectMapper;

    @Override
    @KafkaListener(topics = "${spring.kafka.topics.user-ban-topic.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenEvent(String jsonEvent) {
        System.out.println("Listener invoked" + jsonEvent);
        try {
            UserBanEvent userBanEvent = objectMapper.readValue(jsonEvent, UserBanEvent.class);
            userService.setBannedField(userBanEvent.getUserId(), userBanEvent.isBanned());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
