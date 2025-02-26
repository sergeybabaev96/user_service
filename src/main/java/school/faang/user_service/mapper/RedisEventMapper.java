package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.PremiumBoughtEvent;

import java.time.format.DateTimeFormatter;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface RedisEventMapper {

    default Map<String, Object> toMap(PremiumBoughtEvent event) {
        return Map.of("userId", event.getPremium().getUser().getId(),
                "amount", event.getPaymentResponse().amount(),
                "currency", event.getPaymentResponse().currency().name(),
                "premiumPeriod", event.getPremiumPeriod().name(),
                "startDate", event.getPremium().getStartDate().format(DateTimeFormatter.ISO_DATE_TIME));
    }
}
