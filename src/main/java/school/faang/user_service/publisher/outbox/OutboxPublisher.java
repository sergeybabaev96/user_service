package school.faang.user_service.publisher.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.SubscriptionEventDto;
import school.faang.user_service.entity.outbox.OutboxMessage;
import school.faang.user_service.publisher.subscription.SubscriptionPublisher;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private final SubscriptionPublisher subscriptionPublisher;
    private final ObjectMapper objectMapper;

    public void publish(List<OutboxMessage> messages) {
        messages.forEach(message -> {
            try {
                if ("subscription.follow".equals(message.getEventType())) {
                    SubscriptionEventDto event = objectMapper.readValue(message.getPayload(), SubscriptionEventDto.class);
                    subscriptionPublisher.publishFollowEvent(event);
                } else if ("subscription.unfollow".equals(message.getEventType())) {
                    SubscriptionEventDto event = objectMapper.readValue(message.getPayload(), SubscriptionEventDto.class);
                    subscriptionPublisher.publishUnfollowEvent(event);
                }
            } catch (JsonProcessingException e) {
                log.error("Failed to parse outbox message payload: {}", message.getPayload(), e);
            }
        });
    }
}
