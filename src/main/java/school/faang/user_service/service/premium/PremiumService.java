package school.faang.user_service.service.premium;

import school.faang.user_service.dto.exchange.ExchangeResponseDto;
import school.faang.user_service.dto.premium.PremiumPaymentResponseDto;
import school.faang.user_service.dto.premium.PremiumRequestDto;
import school.faang.user_service.dto.premium.PremiumResponseDto;

import java.util.concurrent.CompletableFuture;

public interface PremiumService {
    CompletableFuture<PremiumResponseDto> buyPremium(PremiumRequestDto premiumRequestDto, boolean byUser);

    CompletableFuture<ExchangeResponseDto> getPremiumPrice(PremiumRequestDto premiumRequestDto);

    void updateAutoRenew(boolean autoRenew, Long userId);

    void premiumRenewal();

    void updatePremium(PremiumPaymentResponseDto premiumPaymentResponse);
}
