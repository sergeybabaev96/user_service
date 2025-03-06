package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.promotion.EventDto;
import school.faang.user_service.dto.promotion.UserDto;
import school.faang.user_service.entity.promotion.event.EventPromotionType;
import school.faang.user_service.entity.promotion.user.UserPromotionType;
import school.faang.user_service.service.PromotionService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/promotion")
public class PromotionController {
    private final PromotionService promotionService;

    @PostMapping("/user/start")
    public void startUserPromotion(@RequestBody UserDto userDto,
                                   @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                   @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                   @RequestParam("promotionType") UserPromotionType promotionType) {
        log.info("Starting promotion for user with ID: {}", userDto.userId());
        promotionService.startUserPromotion(userDto, startDate, endDate, promotionType);
        log.info("Promotion started for user with ID: {}", userDto.userId());
    }

    @PostMapping("/user/end")
    public void endUserPromotion(@RequestBody UserDto userDto,
                                 @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                 @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                 @RequestParam("promotionType") UserPromotionType promotionType) {
        log.info("Ending promotion for user with ID: {}", userDto.userId());
        promotionService.endUserPromotion(userDto, startDate, endDate, promotionType);
        log.info("Promotion ended for user with ID: {}", userDto.userId());
    }

    @PostMapping("/event/start")
    public void startEventPromotion(@RequestBody EventDto eventDto,
                                    @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                    @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                    @RequestParam("promotionType") EventPromotionType promotionType) {
        log.info("Starting promotion for event with ID: {}", eventDto.id());
        promotionService.startEventPromotion(eventDto, startDate, endDate, promotionType);
        log.info("Promotion started for event with ID: {}", eventDto.id());
    }

    @PostMapping("/event/end")
    public void endEventPromotion(@RequestBody EventDto eventDto,
                                  @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                  @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                  @RequestParam("promotionType") EventPromotionType promotionType) {
        log.info("Ending promotion for event with ID: {}", eventDto.id());
        promotionService.endEventPromotion(eventDto, startDate, endDate, promotionType);
        log.info("Promotion ended for event with ID: {}", eventDto.id());
    }

    @GetMapping("/calculate-price/user")
    public BigDecimal calculateUserPromotionPrice(@RequestParam Long userId,
                                                  @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                  @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                                  @RequestParam("promotionType") UserPromotionType promotionType) {
        log.info("Calculating promotion price for user with ID: {}", userId);
        BigDecimal promotionPrice = promotionService.calculateUserPromotionPrice(userId, startDate, endDate, promotionType);
        log.info("Promotion price for user with ID: {} calculated and equals: {}", userId, promotionPrice);
        return promotionPrice;
    }

    @GetMapping("/calculate-price/event")
    public BigDecimal calculateEventPromotionPrice(@RequestParam Long eventId,
                                                   @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                   @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                                   @RequestParam("promotionType") EventPromotionType promotionType) {
        log.info("Calculating promotion price for event with ID: {}", eventId);
        BigDecimal promotionPrice = promotionService.calculateEventPromotionPrice(eventId, startDate, endDate, promotionType);
        log.info("Promotion price for event with ID: {} calculated and equals: {}", eventId, promotionPrice);
        return promotionPrice;
    }
}