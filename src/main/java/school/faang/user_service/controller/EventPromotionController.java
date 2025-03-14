package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.payment.CurrencyDto;
import school.faang.user_service.dto.promotion.EventDto;
import school.faang.user_service.dto.promotion.EventPromotionRequestDto;
import school.faang.user_service.model.promotion.PromotionPriority;
import school.faang.user_service.model.promotion.event.EventPromotionType;
import school.faang.user_service.service.EventPromotionService;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/promotion/event")
public class EventPromotionController {
    private final EventPromotionService eventPromotionService;

    @PostMapping("/start")
    public ResponseEntity<String> startEventPromotion(
            @RequestBody EventDto eventDto,
            @RequestBody EventPromotionRequestDto eventPromotionRequestDto,
            @RequestParam("CurrencyDto") CurrencyDto currencyDto) {

        log.info("Received request to start user promotion. userId: {}, eventPromotionRequestDto: {}",
                eventDto.eventId(), eventPromotionRequestDto);
        ResponseEntity<String> response = eventPromotionService.processStartEventPromotion(eventDto,
                eventPromotionRequestDto, currencyDto);
        log.info("Event promotion started successfully. userID: {}", eventDto.eventId());
        return response;
    }

    @PostMapping("/end")
    public ResponseEntity<String> endEventPromotion(
            @RequestBody EventDto eventDto,
            @RequestBody EventPromotionRequestDto eventPromotionRequestDto) {

        log.info("Received request to end event promotion. userId: {}, eventPromotionRequestDto: {}",
                eventDto.eventId(), eventPromotionRequestDto);
        ResponseEntity<String> response = eventPromotionService.processEndEventPromotion(eventDto,
                eventPromotionRequestDto);
        log.info("Event promotion ended successfully. eventID: {}", eventDto.eventId());
        return response;
    }

    @PostMapping("/update/priority")
    public ResponseEntity<String> updateEventPromotionPriority(
            @RequestBody EventDto eventDto,
            @RequestBody EventPromotionRequestDto eventPromotionRequestDto,
            @RequestParam("CurrencyDto") CurrencyDto currencyDto) {

        log.info("Received request to update event promotion priority. eventId: {}, eventPromotionRequestDto: {}",
                eventDto.eventId(), eventPromotionRequestDto);
        ResponseEntity<String> response = eventPromotionService.processUpdateEventPromotionPriority(eventDto,
                eventPromotionRequestDto, currencyDto);
        log.info("Successfully updated event promotion priority for userId: {}. New promotion priority: {}",
                eventDto.eventId(), eventPromotionRequestDto.promotionPriority());
        return response;
    }

    @PostMapping("/update/type")
    public ResponseEntity<String> updateEventPromotionType(
            @RequestBody EventDto eventDto,
            @RequestBody EventPromotionRequestDto eventPromotionRequestDto,
            @RequestParam("CurrencyDto") CurrencyDto currencyDto) {

        log.info("Received request to update event promotion type. eventId: {}, eventPromotionRequestDto: {}",
                eventDto.eventId(), eventPromotionRequestDto);
        ResponseEntity<String> response = eventPromotionService.processUpdateEventPromotionType(eventDto,
                eventPromotionRequestDto, currencyDto);
        log.info("Successfully updated event promotion type for eventId {}. New promotion type: {} ",
                eventDto.eventId(), eventPromotionRequestDto.eventPromotionType());
        return response;
    }
}
