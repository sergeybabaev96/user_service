package school.faang.user_service.dto.premium;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.dto.payment.CurrencyDto;
import school.faang.user_service.enums.PremiumType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PremiumRequestDto {
    private PremiumType premiumType;
    private Long userId;
    private CurrencyDto currency;
}
