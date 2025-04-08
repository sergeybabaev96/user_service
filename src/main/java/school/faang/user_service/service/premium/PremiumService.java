package school.faang.user_service.service.premium;

import org.springframework.http.ResponseEntity;
import school.faang.user_service.dto.exchange.ExchangeResponseDto;
import school.faang.user_service.dto.premium.PremiumRequestDto;
import school.faang.user_service.dto.premium.PremiumResponseDto;

public interface PremiumService {
    ResponseEntity<PremiumResponseDto> buyPremium(PremiumRequestDto premiumRequestDto);

    ExchangeResponseDto getPremiumPrice(PremiumRequestDto premiumRequestDto);

    void updateAutoRenew(boolean autoRenew, Long userId);

    void premiumRenewal();
}
