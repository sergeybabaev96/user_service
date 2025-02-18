package school.faang.user_service.controller.premium;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.mapper.premium.PremiumMapper;
import school.faang.user_service.service.premium.PremiumService;

@RequiredArgsConstructor
@Validated
@RequestMapping("/premium")
@RestController
public class PremiumController {
    private final UserContext userContext;
    private final PremiumService premiumService;
    private final PremiumMapper premiumMapper;

    @PostMapping("/buy")
    @ResponseStatus(HttpStatus.CREATED)
    public PremiumDto buyPremium(@RequestParam @Positive int days) {

        Long userId = userContext.getUserId();
        Premium premium = premiumService.buyPremium(userId, days);
        return premiumMapper.toDto(premium);
    }
}
