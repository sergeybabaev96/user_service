package school.faang.user_service.dto.premium;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record BuyPremiumDto(
        @NotBlank
        String paymentMethod,
        @Positive
        int days,
        @Positive
        long userId
) {
}
