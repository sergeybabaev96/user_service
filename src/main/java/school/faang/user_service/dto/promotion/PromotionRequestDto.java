package school.faang.user_service.dto.promotion;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.enums.promotion.Currency;
import school.faang.user_service.enums.promotion.PromotionPlanType;
import school.faang.user_service.enums.promotion.PromotionTariff;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PromotionRequestDto {

    @NotNull(message = "User id can't be null")
    @Min(value = 0, message = "User id should be positive")
    private Long userId;

    private Long eventId;

    @NotNull(message = "Tariff can't be null")
    private PromotionTariff tariff;

    @NotNull(message = "Plan type can't be null")
    private PromotionPlanType planType;

    @NotNull(message = "Amount can't be null")
    @Min(value = 1, message = "Amount should be more than 0")
    private BigDecimal amount;

    @NotNull(message = "Currency can't be null")
    private Currency currency;

}
