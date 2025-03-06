package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.service.PromotionService;

@RequiredArgsConstructor
@Controller
public class PromotionController {
    private final PromotionService promotionService;

    public void startUserPromotion(User user) {
        promotionService.startUserPromotion(user);
    }

    public void startEventPromotion(Event event) {
        promotionService.startEventPromotion(event);
    }
}
