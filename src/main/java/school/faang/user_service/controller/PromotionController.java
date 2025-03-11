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
import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.dto.payment.PaymentStatus;
import school.faang.user_service.dto.promotion.EventDto;
import school.faang.user_service.dto.promotion.UserDto;
import school.faang.user_service.model.promotion.PromotionPriority;
import school.faang.user_service.model.promotion.event.EventPromotionType;
import school.faang.user_service.model.promotion.user.UserPromotionType;
import school.faang.user_service.service.EventPromotionService;
import school.faang.user_service.service.UserPromotionService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/promotion")
public class PromotionController {
    private final EventPromotionService eventPromotionService;
    private final UserPromotionService userPromotionService;

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
        ResponseEntity<String> response = userPromotionService.processStartUserPromotion(userDto, startDate,
                endDate, promotionType, promotionPriority);
        log.info("User promotion started successfully. userID: {}", userDto.userId());
        return response;
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
        ResponseEntity<String> response = userPromotionService.processEndUserPromotion(userDto, startDate,
                endDate, promotionType, promotionPriority);
        log.info("User promotion ended successfully. userID: {}", userDto.userId());
        return response;
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
        ResponseEntity<String> response = userPromotionService.processUpdateUserPromotionPriority(userDto, startDate,
                endDate, promotionType, newPromotionPriority);
        log.info("Successfully updated user promotion type for userId: {}. New promotion priority: {} ",
                userDto.userId(), newPromotionPriority);
        return response;
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
        ResponseEntity<String> response = userPromotionService.processUpdateUserPromotionType(userDto, startDate,
                endDate, newPromotionType, promotionPriority);
        log.info("Successfully updated user promotion type for userId: {}. New promotion type: {} ",
                userDto.userId(), newPromotionType);
        return response;
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
        ResponseEntity<String> response = eventPromotionService.processStartEventPromotion(eventDto, startDate,
                endDate, promotionType, promotionPriority);
        log.info("Event promotion started successfully. userID: {}", eventDto.eventId());
        return response;
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
        ResponseEntity<String> response = eventPromotionService.processEndEventPromotion(eventDto, startDate,
                endDate, promotionType, promotionPriority);
        log.info("Event promotion ended successfully. eventID: {}", eventDto.eventId());
        return response;
    }

    @PostMapping("/event/update/type")
    public ResponseEntity<String> updateEventPromotionType(
            @RequestBody EventDto eventDto,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam("promotionType") EventPromotionType newPromotionType,
            @RequestParam("promotionPriority") PromotionPriority promotionPriority) {

        log.info("Received request to update event promotion type. eventID: {}, newPromotionType: {}," +
                        " startDate: {}, endDate: {}, promotionPriority: {}",
                eventDto.eventId(), newPromotionType, startDate, endDate, promotionPriority);
        ResponseEntity<String> response = eventPromotionService.processUpdateEventPromotionType(eventDto, startDate,
                endDate, newPromotionType, promotionPriority);
        log.info("Successfully updated event promotion type for userId: {}. New promotion type: {} " +
                "and priority: {}", eventDto.eventId(), newPromotionType, promotionPriority);
        return response;
    }

    @PostMapping("/event/update/priority")
    public ResponseEntity<String> updateEventPromotionPriority(
            @RequestBody EventDto eventDto,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam("promotionType") EventPromotionType promotionType,
            @RequestParam("promotionPriority") PromotionPriority newPromotionPriority) {

        log.info("Received request to update event promotion priority. eventID: {}, promotionType: {}, " +
                        "startDate: {}, endDate: {}, newPromotionPriority: {}",
                eventDto.eventId(), promotionType, startDate, endDate, newPromotionPriority);
        ResponseEntity<String> response = eventPromotionService.processUpdateEventPromotionPriority(eventDto, startDate,
                endDate, promotionType, newPromotionPriority);
        log.info("Successfully updated event promotion priority for userId: {}. New promotion priority: {}",
                eventDto.eventId(), newPromotionPriority);
        return response;
    }
}