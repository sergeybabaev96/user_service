package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.promotion.EventDto;
import school.faang.user_service.dto.promotion.UserDto;
import school.faang.user_service.service.PromotionService;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Controller
public class PromotionController {
    private final PromotionService promotionService;

    public void startUserPromotion(UserDto userDto, LocalDateTime startDate, LocalDateTime endDate, int percent) {
        log.info("Starting promotion for user with ID: {}", userDto.userId());
        promotionService.startUserPromotion(userDto, startDate, endDate, percent);
        log.info("Promotion started for user with ID: {}", userDto.userId());
    }

    public void endUserPromotion(UserDto userDto, LocalDateTime startDate, LocalDateTime endDate, int percent) {
        log.info("Ending promotion for user with ID: {}", userDto.userId());
        promotionService.endUserPromotion(userDto, startDate, endDate, percent);
        log.info("Promotion ended for user with ID: {}", userDto.userId());
    }

    public void startEventPromotion(EventDto eventDto, LocalDateTime startDate, LocalDateTime endDate, int percent) {
        log.info("Starting promotion for event with ID: {}", eventDto.id());
        promotionService.startEventPromotion(eventDto, startDate, endDate, percent);
        log.info("Promotion started for event with ID: {}", eventDto.id());
    }

    public void endEventPromotion(EventDto eventDto, LocalDateTime startDate, LocalDateTime endDate, int percent) {
        log.info("Ending promotion for event with ID: {}", eventDto.id());
        promotionService.endEventPromotion(eventDto, startDate, endDate, percent);
        log.info("Promotion ended for event with ID: {}", eventDto.id());
    }
}