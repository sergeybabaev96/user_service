package school.faang.user_service.dto.premium;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record BuyPremiumDto(
        @NotBlank(message = "Поле не может быть пустым")
        String paymentMethod,
        @Positive(message = "Значение должно быть положительным числом")
        int days,
        @Positive(message = "Значение Id должно быть положительным числом")
        long userId
) {
}
