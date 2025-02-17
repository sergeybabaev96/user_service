package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.payment.OrderDto;
import school.faang.user_service.dto.promotion.BuyPromotionDto;
import school.faang.user_service.dto.promotion.PromotionDto;
import school.faang.user_service.service.PromotionService;

import java.util.List;

@RestController
@RequiredArgsConstructor

public class PromotionController {

    private final PromotionService promotionService;

    public OrderDto buyPromotion(@Valid @RequestBody BuyPromotionDto buyPromotionDto) {
        return promotionService.buyPromotion(buyPromotionDto);
    }

    public PromotionDto activatePromotion(@PathVariable long orderId, @PathVariable Long promotionId) {
        return promotionService.activatePromotion(orderId, promotionId);
    }

    public List<PromotionDto> getAllPromotionsForUser(@PathVariable Long userId) {
        return promotionService.getAllPromotionsForUser(userId);
    }

    public PromotionDto updatePromotion(@RequestBody BuyPromotionDto buyPromotionDto, @PathVariable Long promotionId) {
        return promotionService.updatePromotion(buyPromotionDto, promotionId);
    }

    public void deletePromotion(@PathVariable Long id) {
        promotionService.deletePromotion(id);
    }
}
