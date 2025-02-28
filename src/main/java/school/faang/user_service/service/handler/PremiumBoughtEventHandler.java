package school.faang.user_service.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.PremiumBoughtEvent;
import school.faang.user_service.redis.RedisEventPublisher;
import school.faang.user_service.mapper.RedisEventMapper;
import school.faang.user_service.redis.event.PremiumBoughtRedisEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class PremiumBoughtEventHandler {

    private final RedisEventPublisher premiumBoughtEventPublisher;
    private final RedisEventMapper redisEventMapper;

    @Value("${user-service.redis.bought-premium-topic}")
    private String topic;

    @Async
    @EventListener
    public void handlePremiumAndSendToRedis(PremiumBoughtEvent event) {
        PremiumBoughtRedisEvent redisEvent = redisEventMapper.toRedisEvent(event);
        premiumBoughtEventPublisher.publish(redisEvent, topic);
        log.info("Premium bought event send to Redis event: {}", redisEvent);
    }
}
