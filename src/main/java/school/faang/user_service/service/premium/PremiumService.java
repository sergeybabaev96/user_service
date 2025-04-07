package school.faang.user_service.service.premium;

import school.faang.user_service.dto.premium.PremiumRequestDto;
import school.faang.user_service.dto.premium.PremiumResponseDto;

public interface PremiumService {
    public PremiumResponseDto buyPremium(PremiumRequestDto premiumRequestDto);
}
