package school.faang.user_service.dto.promotion;

import school.faang.user_service.model.promotion.PromotionPriority;
import school.faang.user_service.model.promotion.event.EventPromotionType;

import java.time.LocalDateTime;

public record EventPromotionRequestDto (
        LocalDateTime startDate,
        LocalDateTime endDate,
        EventPromotionType eventPromotionType,
        PromotionPriority promotionPriority
){
}
