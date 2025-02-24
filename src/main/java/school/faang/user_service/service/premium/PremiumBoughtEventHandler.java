package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.PremiumBoughtEvent;
import school.faang.user_service.redis.PremiumBoughtEventPublisher;
import school.faang.user_service.redis.event.RedisEvent;

import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PremiumBoughtEventHandler {
    private static final String PREMIUM_BOUGHT = "PREMIUM_BOUGHT";

    private final PremiumBoughtEventPublisher premiumBoughtEventPublisher;

    @Async
    @EventListener
    public void handlePremiumAndSendToRedis(PremiumBoughtEvent event) {
        RedisEvent redisEvent = new RedisEvent();
        redisEvent.setType(PREMIUM_BOUGHT);
        redisEvent.setData(Map.of("userId", event.getPremium().getUser().getId(),
                "amount", event.getPaymentResponse().amount(),
                "currency", event.getPaymentResponse().currency().name(),
                "premiumPeriod", event.getPremiumPeriod().name(),
                "startDate", event.getPremium().getStartDate().format(DateTimeFormatter.ISO_DATE_TIME)));
        premiumBoughtEventPublisher.publish(redisEvent);
        log.info("Premium bought event send to Redis event: {}", redisEvent);
    }
}
