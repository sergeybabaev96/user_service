package school.faang.user_service.controller.premium;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.enums.PremiumPeriod;
import school.faang.user_service.service.PremiumService;

@RestController
@RequestMapping("/premium")
@RequiredArgsConstructor
public class PremiumController {

    private final PremiumService premiumService;

    @PostMapping("/buy")
    public ResponseEntity<PremiumDto> buyPremium(@RequestParam int days, @RequestParam long userId) {
        PremiumPeriod premiumPeriod = PremiumPeriod.fromDays(days);
        return ResponseEntity.ok(premiumService.buyPremium(userId, premiumPeriod));
    }
}
