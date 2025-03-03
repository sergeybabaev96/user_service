package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.PremiumBoughtEvent;
import school.faang.user_service.mapper.RedisEventMapper;
import school.faang.user_service.properties.UserServiceProperties;
import school.faang.user_service.redis.RedisEventPublisher;
import school.faang.user_service.redis.event.PremiumBoughtRedisEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class PremiumBoughtEventHandler {

    private final RedisEventPublisher premiumBoughtEventPublisher;
    private final RedisEventMapper redisEventMapper;
    private final UserServiceProperties properties;

    private final String topic = properties.getRedis().getChannel().getBoughtPremiumTopic();

    @Async
    @EventListener
    public void handlePremiumAndSendToRedis(PremiumBoughtEvent event) {
        PremiumBoughtRedisEvent redisEvent = redisEventMapper.toRedisEvent(event);
        premiumBoughtEventPublisher.publish(redisEvent, topic);
        log.info("Premium bought event send to Redis event: {}", redisEvent);
    }
}
