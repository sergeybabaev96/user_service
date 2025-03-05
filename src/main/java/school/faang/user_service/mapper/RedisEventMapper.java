package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.ProfilePicEvent;
import school.faang.user_service.redis.event.ProfilePicRedisEvent;
import school.faang.user_service.dto.PremiumBoughtEvent;
import school.faang.user_service.redis.event.PremiumBoughtRedisEvent;

import java.time.format.DateTimeFormatter;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface RedisEventMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "picKey", source = "profilePicUrl")
    ProfilePicRedisEvent toRedisEvent(ProfilePicEvent event);

    @Mapping(target = "type", constant = "PREMIUM_BOUGHT")
    @Mapping(target = "data", expression = "java(toMap(event))")
    PremiumBoughtRedisEvent toRedisEvent(PremiumBoughtEvent event);

    default Map<String, Object> toMap(PremiumBoughtEvent event) {
        return Map.of("userId", event.getPremium().getUser().getId(),
                "amount", event.getPaymentResponse().amount(),
                "currency", event.getPaymentResponse().currency().name(),
                "premiumPeriod", event.getPremiumPeriod().name(),
                "startDate", event.getPremium().getStartDate().format(DateTimeFormatter.ISO_DATE_TIME));
    }
}
