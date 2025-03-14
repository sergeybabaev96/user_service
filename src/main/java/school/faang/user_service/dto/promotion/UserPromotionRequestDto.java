package school.faang.user_service.dto.promotion;

import school.faang.user_service.model.promotion.PromotionPriority;
import school.faang.user_service.model.promotion.user.UserPromotionType;

import java.time.LocalDateTime;

public record UserPromotionRequestDto(
        LocalDateTime startDate,
        LocalDateTime endDate,
        UserPromotionType userPromotionType,
        PromotionPriority promotionPriority
){
}
