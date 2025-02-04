package school.faang.user_service.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.PremiumDto;
import school.faang.user_service.service.PremiumService;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("${user-service.api-version}/premium")
public class PremiumController {
    private final PremiumService premiumService;

    @PutMapping(value = "/buy/{days}")
    public PremiumDto buyPremium(@Positive(message = "Days must be positive") @PathVariable Integer days) {
        return premiumService.buyPremium(days);
    }
}
