package school.faang.user_service.client.payment;

import jakarta.validation.constraints.NotNull;
import school.faang.user_service.common.Currency;
import school.faang.user_service.common.PaymentStatus;

import java.math.BigDecimal;

public record PaymentResponse(
        @NotNull
        Long id,
        PaymentStatus status,
        int verificationCode,
        long paymentNumber,
        BigDecimal amount,
        Currency currency,
        String message
) {
}