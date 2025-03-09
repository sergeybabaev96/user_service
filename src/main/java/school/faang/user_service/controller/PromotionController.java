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
import school.faang.user_service.exception.DuplicatePromotionException;
import school.faang.user_service.model.promotion.PromotionPriority;
import school.faang.user_service.model.promotion.event.EventPromotionType;
import school.faang.user_service.model.promotion.user.UserPromotionType;
import school.faang.user_service.service.PromotionService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/promotion")
public class PromotionController {
    public static final String PAYMENT_FAILED_FOR_USER = "Payment failed for user with ID: {}";
    public static final String PAYMENT_SUCCESSFUL_FOR_USER = "Payment successful for user with ID: {}";
    public static final String CALCULATED_PRICE_DIFFERENCE_FOR_USER = "Calculated priceDifference for userID: {} is: {}";
    public static final String PAYMENT_FAILED_FOR_EVENT = "Payment failed for event with ID: {}";
    public static final String PAYMENT_SUCCESSFUL_FOR_EVENT = "Payment successful for event with ID: {}";
    public static final String CALCULATED_PRICE_DIFFERENCE_FOR_EVENT = "Calculated priceDifference for eventID: {} is: {}";
    private final PromotionService promotionService;
    private final RestTemplate restTemplate;

    @PostMapping("/user/start")
    public ResponseEntity<String> startUserPromotion(
            @RequestBody UserDto userDto,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam("promotionType") UserPromotionType promotionType,
            @RequestParam("promotionPriority") PromotionPriority promotionPriority) {
        log.info("Received request to start user promotion. userID: {}, promotionType: {}," +
                        " startDate: {}, endDate: {}, promotionPriority: {}",
                userDto.userId(), promotionType, startDate, endDate, promotionPriority);
        BigDecimal promotionPrice = promotionService.calculateUserPromotionPrice(userDto.userId(),
                startDate, endDate, promotionType.getUserPercentage(), promotionPriority.getFeedRank());
        log.info("Calculated promotion price for userID: {} is: {}", userDto.userId(), promotionPrice);

        PaymentResponse paymentResponse = processPayment(userDto.userId(), promotionPrice);
        if (paymentResponse == null || paymentResponse.status() != PaymentStatus.SUCCESS) {
            log.error(PAYMENT_FAILED_FOR_USER, userDto.userId());
            return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body("Payment failed for user promotion");
        }
        log.info(PAYMENT_SUCCESSFUL_FOR_USER, userDto.userId());
        promotionService.updateUserPromotionCount(userDto.userId(), promotionPrice);

        try {
            promotionService.startUserPromotion(userDto, startDate, endDate, promotionType, promotionPriority);
        } catch (DuplicatePromotionException ex) {
            log.error("Error starting user promotion for userID: {}: {}", userDto.userId(), ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        log.info("User promotion started successfully. userID: {}", userDto.userId());
        return ResponseEntity.ok("User promotion started successfully");
    }

    @PostMapping("/user/end")
    public ResponseEntity<String> endUserPromotion(
            @RequestBody UserDto userDto,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam("promotionType") UserPromotionType promotionType,
            @RequestParam("promotionPriority") PromotionPriority promotionPriority) {
        log.info("Received request to end user promotion. userID: {}, promotionType: {}," +
                        " startDate: {}, endDate: {}, promotionPriority: {}",
                userDto.userId(), promotionType, startDate, endDate, promotionPriority);
        try {
            promotionService.endUserPromotion(userDto, startDate, endDate, promotionType, promotionPriority);
        } catch (IllegalArgumentException ex) {
            log.error("Error ending user promotion. userID: {}: {}", userDto.userId(), ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        log.info("User promotion ended successfully. userID: {}", userDto.userId());
        return ResponseEntity.ok("User promotion ended successfully");
    }

    @PostMapping("/user/update/priority")
    public ResponseEntity<String> updateUserPromotionPriority(
            @RequestBody UserDto userDto,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam("promotionType") UserPromotionType promotionType,
            @RequestParam("promotionPriority") PromotionPriority newPromotionPriority) {
        log.info("Received request to update user promotion priority. userID: {}, promotionType:" +
                        " {}, startDate: {}, endDate: {}, newPromotionPriority: {}",
                userDto.userId(), promotionType, startDate, endDate, newPromotionPriority);
        BigDecimal priceDifference = promotionService.calculateUserPromotionPriceDifferenceOnPriorityChange(
                userDto.userId(), startDate, endDate, promotionType, newPromotionPriority);

        log.info(CALCULATED_PRICE_DIFFERENCE_FOR_USER, userDto.userId(), priceDifference);
        if (priceDifference.compareTo(BigDecimal.ZERO) > 0) {
            PaymentResponse paymentResponse = processPayment(userDto.userId(), priceDifference);
            if (paymentResponse == null || paymentResponse.status() != PaymentStatus.SUCCESS) {
                log.error(PAYMENT_FAILED_FOR_USER, userDto.userId());
                return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body("Payment failed for promotion update");
            }
            log.info(PAYMENT_SUCCESSFUL_FOR_USER, userDto.userId());
        } else {
            //return money
        }
        promotionService.updateUserPromotionPriority(userDto.userId(), startDate, endDate,
                promotionType, newPromotionPriority);
        log.info("Successfully updated user promotion type for userId: {} with new promotion priority: {} ",
                userDto.userId(), newPromotionPriority);
        return ResponseEntity.ok("User promotion priority updated successfully");
    }

    @PostMapping("/user/update/type")
    public ResponseEntity<String> updateUserPromotionType(
            @RequestBody UserDto userDto,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam("promotionType") UserPromotionType newPromotionType,
            @RequestParam("promotionPriority") PromotionPriority promotionPriority) {
        log.info("Received request to update user promotion type. userID: {}, newPromotionType: {}," +
                        " startDate: {}, endDate: {}, promotionPriority: {}",
                userDto.userId(), newPromotionType, startDate, endDate, promotionPriority);
        BigDecimal priceDifference = promotionService.calculateUserPromotionPriceDifferenceOnTypeChange(
                userDto.userId(), startDate, endDate, newPromotionType, promotionPriority);

        log.info(CALCULATED_PRICE_DIFFERENCE_FOR_USER, userDto.userId(), priceDifference);
        if (priceDifference.compareTo(BigDecimal.ZERO) > 0) {
            PaymentResponse paymentResponse = processPayment(userDto.userId(), priceDifference);
            if (paymentResponse == null || paymentResponse.status() != PaymentStatus.SUCCESS) {
                log.error(PAYMENT_FAILED_FOR_USER, userDto.userId());
                return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body("Payment failed for promotion update");
            }
            log.info(PAYMENT_SUCCESSFUL_FOR_USER, userDto.userId());
        } else {
            //return money
        }
        promotionService.updateUserPromotionType(userDto.userId(),
                startDate, endDate, newPromotionType, promotionPriority);
        log.info("Successfully updated user promotion type for userId: {} with new promotion type: {} ",
                userDto.userId(), newPromotionType);
        return ResponseEntity.ok("User promotion updated successfully");
    }

    @PostMapping("/event/start")
    public ResponseEntity<String> startEventPromotion(
            @RequestBody EventDto eventDto,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam("promotionType") EventPromotionType promotionType,
            @RequestParam("promotionPriority") PromotionPriority promotionPriority) {
        log.info("Received request to start event promotion. eventID: {}, promotionType: {}," +
                        " startDate: {}, endDate: {}, promotionPriority: {}",
                eventDto.eventId(), promotionType, startDate, endDate, promotionPriority);
        BigDecimal promotionPrice = promotionService.calculateEventPromotionPrice(eventDto.eventId(), startDate,
                endDate, promotionType.getEventPercentage(), promotionPriority.getFeedRank());
        log.info("Calculated promotion price for eventID: {} is: {}", eventDto.eventId(), promotionPrice);

        PaymentResponse paymentResponse = processPayment(eventDto.eventId(), promotionPrice);
        if (paymentResponse == null || paymentResponse.status() != PaymentStatus.SUCCESS) {
            log.error(PAYMENT_FAILED_FOR_EVENT, eventDto.eventId());
            return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body("Payment failed for event promotion");
        }
        log.info(PAYMENT_SUCCESSFUL_FOR_EVENT, eventDto.eventId());
        promotionService.updateEventPromotionCount(eventDto.eventId(), promotionPrice);

        try {
            promotionService.startEventPromotion(eventDto, startDate, endDate, promotionType, promotionPriority);
        } catch (DuplicatePromotionException ex) {
            log.error("Error starting event promotion for eventID: {}: {}", eventDto.eventId(), ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        log.info("Event promotion started successfully. userID: {}", eventDto.eventId());
        return ResponseEntity.ok("Event promotion started successfully");
    }

    @PostMapping("/event/end")
    public ResponseEntity<String> endEventPromotion(
            @RequestBody EventDto eventDto,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam("promotionType") EventPromotionType promotionType,
            @RequestParam("promotionPriority") PromotionPriority promotionPriority) {
        log.info("Received request to end event promotion. eventID: {}, promotionType: {}, " +
                        "startDate: {}, endDate: {}, promotionPriority: {}",
                eventDto.eventId(), promotionType, startDate, endDate, promotionPriority);
        try {
            promotionService.endEventPromotion(eventDto, startDate, endDate, promotionType, promotionPriority);
        } catch (IllegalArgumentException ex) {
            log.error("Error ending event promotion. eventID: {}: {}", eventDto.eventId(), ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        log.info("Event promotion ended successfully. eventID: {}", eventDto.eventId());
        return ResponseEntity.ok("Event promotion ended successfully");
    }

    @PostMapping("/event/update/type")
    public ResponseEntity<String> updateEventPromotionType(
            @RequestBody EventDto eventDto,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam("promotionType") UserPromotionType newPromotionType,
            @RequestParam("promotionPriority") PromotionPriority promotionPriority) {
        log.info("Received request to update event promotion type. eventID: {}, newPromotionType: {}," +
                        " startDate: {}, endDate: {}, promotionPriority: {}",
                eventDto.eventId(), newPromotionType, startDate, endDate, promotionPriority);
        BigDecimal priceDifference = promotionService.calculateEventPromotionPriceDifferenceOnTypeChange(
                eventDto.eventId(), startDate, endDate, newPromotionType, promotionPriority);

        log.info("Calculated priceDifference for eventID: {} is: {}", eventDto.eventId(), priceDifference);
        if (priceDifference.compareTo(BigDecimal.ZERO) > 0) {
            PaymentResponse paymentResponse = processPayment(eventDto.eventId(), priceDifference);
            if (paymentResponse == null || paymentResponse.status() != PaymentStatus.SUCCESS) {
                log.error(PAYMENT_FAILED_FOR_EVENT, eventDto.eventId());
                return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body("Payment failed for promotion update");
            }
            log.info(PAYMENT_SUCCESSFUL_FOR_EVENT, eventDto.eventId());
        } else {
            //return money
        }
        promotionService.updateEventPromotionType(eventDto.eventId(),
                startDate, endDate, newPromotionType, promotionPriority);
        log.info("Successfully updated event promotion type for userId: {} with new promotion type: {} " +
                "and priority: {}", eventDto.eventId(), newPromotionType, promotionPriority);
        return ResponseEntity.ok("Event promotion updated successfully");
    }

    @PostMapping("/event/update/priority")
    public ResponseEntity<String> updateEventPromotionPriority(
            @RequestBody EventDto eventDto,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam("promotionType") UserPromotionType promotionType,
            @RequestParam("promotionPriority") PromotionPriority newPromotionPriority) {
        log.info("Received request to update event promotion priority. eventID: {}, promotionType: {}, " +
                        "startDate: {}, endDate: {}, newPromotionPriority: {}",
                eventDto.eventId(), promotionType, startDate, endDate, newPromotionPriority);
        BigDecimal priceDifference = promotionService.calculateEventPromotionPriceDifferenceOnPriorityChange(
                eventDto.eventId(), startDate, endDate, promotionType, newPromotionPriority);

        log.info(CALCULATED_PRICE_DIFFERENCE_FOR_EVENT, eventDto.eventId(), priceDifference);
        if (priceDifference.compareTo(BigDecimal.ZERO) > 0) {
            PaymentResponse paymentResponse = processPayment(eventDto.eventId(), priceDifference);
            if (paymentResponse == null || paymentResponse.status() != PaymentStatus.SUCCESS) {
                log.error(PAYMENT_FAILED_FOR_EVENT, eventDto.eventId());
                return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body("Payment failed for promotion update");
            }
            log.info(PAYMENT_SUCCESSFUL_FOR_EVENT, eventDto.eventId());
        } else {
            //return money
        }
        promotionService.updateEventPromotionPriority(eventDto.eventId(), startDate, endDate,
                promotionType, newPromotionPriority);
        log.info("Successfully updated event promotion type for userId: {} with new promotion priority: {}",
                eventDto.eventId(), newPromotionPriority);
        return ResponseEntity.ok("User promotion priority updated successfully");
    }

    private PaymentResponse processPayment(Long entityId, BigDecimal amount) {
        PaymentRequest paymentRequest = new PaymentRequest(entityId, amount, Currency.USD);
        log.info("Initiating payment request: {}", paymentRequest);
        return restTemplate.postForObject("http://localhost:9081/api/payment",
                paymentRequest, PaymentResponse.class);
    }
}