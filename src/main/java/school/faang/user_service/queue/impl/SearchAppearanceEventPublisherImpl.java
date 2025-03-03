package school.faang.user_service.queue.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.qualifiers.FilterUserChannelQualifier;
import school.faang.user_service.dto.queue.SearchAppearanceEvent;
import school.faang.user_service.queue.SearchAppearanceEventPublisher;

@Slf4j
@Component
public class SearchAppearanceEventPublisherImpl implements SearchAppearanceEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic filterUserChannelTopic;
    private final ObjectMapper objectMapper;

    @Autowired
    public SearchAppearanceEventPublisherImpl(
            RedisTemplate<String, Object> redisTemplate,
            @FilterUserChannelQualifier ChannelTopic filterUserChannelTopic,
            @Qualifier("objectMapper") ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.filterUserChannelTopic = filterUserChannelTopic;
        this.objectMapper = objectMapper;
    }



    @Override
    public void publish(SearchAppearanceEvent searchAppearanceEvent) {
        try {
            redisTemplate.convertAndSend(
                filterUserChannelTopic.getTopic(),
                objectMapper.writeValueAsString(searchAppearanceEvent)
            );
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }
}