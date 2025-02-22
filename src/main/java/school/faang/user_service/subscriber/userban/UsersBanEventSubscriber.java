package school.faang.user_service.subscriber.userban;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.redis.Channels;
import school.faang.user_service.dto.event.EventUsersBan;
import school.faang.user_service.exception.MessageMappingException;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.subscriber.EventSubscriber;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class UsersBanEventSubscriber implements EventSubscriber {

    private final ObjectMapper objectMapper;
    private final EventService eventService;
    private final Channels channels;

    public String getTopicName() {
        return channels.getUserBanChannel();
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            log.info("Received an event to ban users");
            EventUsersBan usersBanEvent = objectMapper.readValue(message.getBody(), EventUsersBan.class);
            eventService.banUsers(usersBanEvent.userIdsToBan());
        } catch (IOException ex) {
            throw new MessageMappingException("Failed to map message to UsersBanEvent", ex);
        }
    }
}
