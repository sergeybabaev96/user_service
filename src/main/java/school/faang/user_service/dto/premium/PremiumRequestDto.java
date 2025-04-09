package school.faang.user_service.dto.premium;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.dto.payment.CurrencyDto;
import school.faang.user_service.enums.premium.PremiumType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PremiumRequestDto {
    @NotNull
    private PremiumType premiumType;

    @NotNull
    private Long userId;

    @NotNull
    private CurrencyDto selectedCurrency;
    private boolean autoRenew;
}
