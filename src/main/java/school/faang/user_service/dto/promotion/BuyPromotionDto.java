package school.faang.user_service.dto.promotion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.promotion.PromotionPlan;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class  BuyPromotionDto {
    @NotBlank(message = "Поле способа оплаты не может быть пустым")
    private String paymentMethod;
    @Positive(message = "Поле Id должно быть положительным числом")
    private long userId;
    @NotNull(message = "Поле не может равняться нулю")
    private PromotionPlan plan;
}
