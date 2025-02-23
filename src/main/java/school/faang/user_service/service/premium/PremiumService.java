package school.faang.user_service.service.premium;

import school.faang.user_service.dto.PremiumDto;

public interface PremiumService {
    PremiumDto buyPremium(Integer days);

    void deleteExpiredPremiumsAsync();
}
