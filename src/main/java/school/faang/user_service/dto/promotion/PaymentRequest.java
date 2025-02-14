package school.faang.user_service.dto.promotion;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import school.faang.user_service.enums.promotion.Currency;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record PaymentRequest(
        @NotNull(message = "Payment number can't be null")
        UUID paymentNumber,

        @Min(value = 1, message = "Amount should be more than 0")
        @NotNull
        BigDecimal amount,

        @NotNull(message = "Currency can't be null")
        Currency currency
) {
}
