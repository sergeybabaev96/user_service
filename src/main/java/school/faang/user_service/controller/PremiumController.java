package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.premium.PremiumRequestDto;
import school.faang.user_service.dto.premium.PremiumResponseDto;
import school.faang.user_service.service.premium.PremiumService;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PremiumController {
    private final PremiumService premiumService;

    public PremiumResponseDto buyPremium(@RequestBody PremiumRequestDto premiumRequestDto) {
        log.info("Received request to purchase premium for user with ID {}", premiumRequestDto.getUserId());
        return premiumService.buyPremium(premiumRequestDto);
    }
}
