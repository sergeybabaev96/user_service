package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import school.faang.user_service.dto.payment.Currency;
import school.faang.user_service.dto.payment.PaymentRequest;
import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.dto.payment.PaymentStatus;
import school.faang.user_service.dto.promotion.EventDto;
import school.faang.user_service.dto.promotion.UserDto;
import school.faang.user_service.service.PromotionService;
import school.faang.user_service.service.promotion.PromotionPriority;
import school.faang.user_service.service.promotion.event.EventPromotionType;
import school.faang.user_service.service.promotion.user.UserPromotionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/promotion")
public class PromotionController {
    private final PromotionService promotionService;
    private final RestTemplate restTemplate;

    @PostMapping("/user/start")
    public ResponseEntity<String> startUserPromotion(@RequestBody UserDto userDto,
                                                     @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                     @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                                     @RequestParam("promotionType") UserPromotionType promotionType,
                                                     @RequestParam("promotionPriority") PromotionPriority promotionPriority) {
        log.info("Received request to start user process promotion. userID: {}, promotionType: {}, startDate: {}, endDate: {}",
                userDto.userId(), promotionType, startDate, endDate);
        BigDecimal promotionPrice = promotionService.calculateUserPromotionPrice(
                userDto.userId(), startDate, endDate, promotionType, promotionPriority);
        log.info("Calculated promotion price for userID: {} is: {}", userDto.userId(), promotionPrice);

        PaymentResponse paymentResponse = processPayment(userDto.userId(), promotionPrice);
        if (paymentResponse == null || paymentResponse.status() != PaymentStatus.SUCCESS) {
            log.error("Payment failed for user with ID: {}", userDto.userId());
            return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body("Payment failed for user promotion");
        }
        log.info("Payment successful for user with ID: {}", userDto.userId());
        promotionService.updateUserPromotionCount(userDto.userId(), promotionPrice);
        try {
            promotionService.startUserPromotion(userDto, startDate, endDate, promotionType, promotionPriority);
        } catch (IllegalArgumentException ex) {
            log.error("Error starting user promotion for userID: {}: {}", userDto.userId(), ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        return ResponseEntity.ok("User promotion started successfully");
    }

    @PostMapping("/user/end")
    public ResponseEntity<String> endUserPromotion(@RequestBody UserDto userDto,
                                                   @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                   @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                                   @RequestParam("promotionType") UserPromotionType promotionType,
                                                   @RequestParam("promotionPriority") PromotionPriority promotionPriority) {
        log.info("Received request to end user promotion. userID: {}, promotionType: {}, startDate: {}, endDate: {}",
                userDto.userId(), promotionType, startDate, endDate);
        try {
            promotionService.endUserPromotion(userDto, startDate, endDate, promotionType, promotionPriority);
        } catch (IllegalArgumentException ex) {
            log.error("Error ending user promotion. userID: {}: {}", userDto.userId(), ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        log.info("User promotion ended successfully. userID: {}", userDto.userId());
        return ResponseEntity.ok("User promotion ended successfully");
    }

    @PostMapping("/event/start")
    public ResponseEntity<String> startEventPromotion(@RequestBody EventDto eventDto,
                                                      @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                      @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                                      @RequestParam("promotionType") EventPromotionType promotionType,
                                                      @RequestParam("promotionPriority") PromotionPriority promotionPriority) {
        log.info("Received request to start event promotion. eventID: {}, promotionType: {}, startDate: {}, endDate: {}",
                eventDto.eventId(), promotionType, startDate, endDate);
        BigDecimal promotionPrice = promotionService.calculateEventPromotionPrice(eventDto.eventId(), startDate, endDate, promotionType, promotionPriority);
        log.info("Calculated promotion price for eventID: {} is: {}", eventDto.eventId(), promotionPrice);

        PaymentResponse paymentResponse = processPayment(eventDto.eventId(), promotionPrice);
        if (paymentResponse == null || paymentResponse.status() != PaymentStatus.SUCCESS) {
            log.error("Payment failed for event with ID: {}", eventDto.eventId());
            return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body("Payment failed for event promotion");
        }
        log.info("Payment successful for event with ID: {}", eventDto.eventId());
        promotionService.updateEventPromotionCount(eventDto.eventId(), promotionPrice);
        try {
            promotionService.startEventPromotion(eventDto, startDate, endDate, promotionType, promotionPriority);
        } catch (IllegalArgumentException ex) {
            log.error("Error starting event promotion for eventID: {}: {}", eventDto.eventId(), ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        return ResponseEntity.ok("Event promotion started successfully");
    }

    @PostMapping("/event/end")
    public ResponseEntity<String> endEventPromotion(@RequestBody EventDto eventDto,
                                                    @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                    @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                                    @RequestParam("promotionType") EventPromotionType promotionType,
                                                    @RequestParam("promotionPriority") PromotionPriority promotionPriority) {
        log.info("Received request to end event promotion. eventID: {}, promotionType: {}, startDate: {}, endDate: {}",
                eventDto.eventId(), promotionType, startDate, endDate);
        try {
            promotionService.endEventPromotion(eventDto, startDate, endDate, promotionType, promotionPriority);
        } catch (IllegalArgumentException ex) {
            log.error("Error ending event promotion. eventID: {}: {}", eventDto.eventId(), ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        log.info("Event promotion ended successfully. eventID: {}", eventDto.eventId());
        return ResponseEntity.ok("Event promotion ended successfully");
    }

    private PaymentResponse processPayment(Long entityId, BigDecimal amount) {
        PaymentRequest paymentRequest = new PaymentRequest(entityId, amount, Currency.RUB);
        log.info("Initiating payment request: {}", paymentRequest);
        return restTemplate.postForObject("http://localhost:9081/api/payment", paymentRequest, PaymentResponse.class);
    }
}