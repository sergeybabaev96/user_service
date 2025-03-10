package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import school.faang.user_service.dto.payment.Currency;
import school.faang.user_service.dto.payment.PaymentRequest;
import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.dto.payment.PaymentStatus;
import school.faang.user_service.dto.promotion.EventDto;
import school.faang.user_service.entity.promotion.event.EventPromotion;
import school.faang.user_service.entity.promotion.event.EventPromotionCount;
import school.faang.user_service.exception.DuplicatePromotionException;
import school.faang.user_service.exception.PromotionNotFoundException;
import school.faang.user_service.model.promotion.PromotionPriority;
import school.faang.user_service.model.promotion.event.EventPromotionPricing;
import school.faang.user_service.model.promotion.event.EventPromotionType;
import school.faang.user_service.model.promotion.user.UserPromotionType;
import school.faang.user_service.repository.promotion.EventPromotionCountRepository;
import school.faang.user_service.repository.promotion.EventPromotionRepository;
import school.faang.user_service.utils.validatonUtils.PromotionValidation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventPromotionService {
    public static final String EVENT_DTO_CANNOT_BE_NULL = "Event DTO can't be null";
    public static final String DUPLICATE_EVENT_PROMOTION_MESSAGE = "Event promotion for eventId=%d with" +
            " startDate=%s, endDate=%s already exists in DB";
    public static final String NO_EVENT_PROMOTION_FOUND = "No promotion found for eventId=%d, startDate=%s," +
            " endDate=%s, userPercentage=%d and feedRank=%d";
    private static final String CANT_UPDATE_EVENT_PROMOTION_TYPE = "Can't update promotionType for eventId=%d," +
            " startDate=%s, endDate=%s and promotionPriority=%s.";
    private static final String CANT_UPDATE_EVENT_PROMOTION_PRIORITY = "Can't update promotionPriority for " +
            "eventId=%d, startDate=%s, endDate=%s and promotionType=%s.";
    public static final String PAYMENT_FAILED_FOR_EVENT = "Payment failed for event with ID: {}";
    public static final String PAYMENT_SUCCESSFUL_FOR_EVENT = "Payment successful for event with ID: {}";
    public static final String CALCULATED_PRICE_DIFFERENCE_FOR_EVENT = "Calculated priceDifference for eventID: {} is: {}";

    private static final BigDecimal EVENT_PROMOTION_PRICE_DECREASE = new BigDecimal("0.02");
    private static final BigDecimal MAX_DISCOUNT = new BigDecimal("0.20");
    private static final BigDecimal DISCOUNT_THRESHOLD = new BigDecimal("50000");
    private static final BigDecimal EVENT_PROMOTION_PRICE_PER_MINUTE = new BigDecimal("7");
    private static final BigDecimal SECONDS_IN_MINUTE = new BigDecimal("60");

    private final RestTemplate restTemplate;
    private final EventPromotionRepository eventPromotionRepository;
    private final EventPromotionCountRepository eventPromotionCountRepository;

    private PaymentResponse processPayment(Long entityId, BigDecimal amount) {
        PaymentRequest paymentRequest = new PaymentRequest(entityId, amount, Currency.USD);
        log.info("Initiating payment request: {}", paymentRequest);
        return restTemplate.postForObject("http://localhost:9081/api/payment",
                paymentRequest, PaymentResponse.class);
    }

    public ResponseEntity<String> processStartEventPromotion(
            EventDto eventDto, LocalDateTime startDate, LocalDateTime endDate,
            EventPromotionType promotionType, PromotionPriority promotionPriority) {

        validateEventDto(eventDto);
        PromotionValidation.validateEventPromotion(eventDto.eventId(), startDate,
                endDate, promotionType, promotionPriority);
        BigDecimal promotionPrice = calculateEventPromotionPrice(eventDto.eventId(), startDate,
                endDate, promotionType.getUserPercentage(), promotionPriority.getFeedRank());
        log.info("Calculated promotion price for eventID: {} is: {}", eventDto.eventId(), promotionPrice);

        PaymentResponse paymentResponse = processPayment(eventDto.eventId(), promotionPrice);
        if (paymentResponse == null || paymentResponse.status() != PaymentStatus.SUCCESS) {
            log.error(PAYMENT_FAILED_FOR_EVENT, eventDto.eventId());
            return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body("Payment failed for event promotion");
        }
        log.info(PAYMENT_SUCCESSFUL_FOR_EVENT, eventDto.eventId());
        updateEventPromotionCount(eventDto.eventId(), promotionPrice);

        try {
            startEventPromotion(eventDto, startDate, endDate, promotionType, promotionPriority);
        } catch (DuplicatePromotionException ex) {
            log.error("Error starting event promotion for eventID: {}: {}", eventDto.eventId(), ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        return ResponseEntity.ok("Event promotion started successfully");
    }

    public ResponseEntity<String> processEndEventPromotion(
            EventDto eventDto, LocalDateTime startDate, LocalDateTime endDate,
            EventPromotionType promotionType, PromotionPriority promotionPriority) {

        validateEventDto(eventDto);
        PromotionValidation.validateEventPromotion(eventDto.eventId(), startDate,
                endDate, promotionType, promotionPriority);
        try {
            endEventPromotion(eventDto, startDate, endDate, promotionType, promotionPriority);
        } catch (IllegalArgumentException ex) {
            log.error("Error ending event promotion. eventID: {}: {}", eventDto.eventId(), ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        return ResponseEntity.ok("Event promotion ended successfully");
    }

    public ResponseEntity<String> processUpdateEventPromotionType(
            EventDto eventDto, LocalDateTime startDate, LocalDateTime endDate,
            EventPromotionType newPromotionType, PromotionPriority promotionPriority) {

        validateEventDto(eventDto);
        PromotionValidation.validateEventPromotion(eventDto.eventId(), startDate,
                endDate, newPromotionType, promotionPriority);
        BigDecimal priceDifference = calculateEventPromotionPriceDifferenceOnTypeChange(
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
        eventPromotionRepository.updatePromotionPercentage(eventDto.eventId(), startDate, endDate,
                newPromotionType.getUserPercentage(), promotionPriority.getFeedRank());
        return ResponseEntity.ok("Event promotion updated successfully");
    }

    public ResponseEntity<String> processUpdateEventPromotionPriority(
            EventDto eventDto, LocalDateTime startDate, LocalDateTime endDate,
            EventPromotionType promotionType, PromotionPriority newPromotionPriority) {

        validateEventDto(eventDto);
        PromotionValidation.validateEventPromotion(eventDto.eventId(), startDate,
                endDate, promotionType, newPromotionPriority);
        BigDecimal priceDifference = calculateEventPromotionPriceDifferenceOnPriorityChange(
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
        eventPromotionRepository.updateEventFeedRank(eventDto.eventId(), startDate, endDate,
                promotionType.getUserPercentage(), newPromotionPriority.getFeedRank());
        return ResponseEntity.ok("User promotion priority updated successfully");
    }

    public void startEventPromotion(EventDto eventDto, LocalDateTime startDate, LocalDateTime endDate,
                                    EventPromotionType promotionType, PromotionPriority promotionPriority) {

        int promotionViewsPercentage = promotionType.getUserPercentage();
        int feedRank = promotionPriority.getFeedRank();
        EventPromotion eventPromotion = eventPromotionRepository.findPromotionByEventIdStartDateEndDate(
                eventDto.eventId(), startDate, endDate);
        if (eventPromotion != null) {
            String message = String.format(DUPLICATE_EVENT_PROMOTION_MESSAGE,
                    eventDto.eventId(), startDate, endDate);
            log.error(message);
            throw new DuplicatePromotionException(message);
        }

        EventPromotion eventPromotionToSave = new EventPromotion();
        eventPromotionToSave.setEventId(eventDto.eventId());
        eventPromotionToSave.setPercentage(promotionViewsPercentage);
        eventPromotionToSave.setStartDate(startDate);
        eventPromotionToSave.setEndDate(endDate);
        eventPromotionToSave.setFeedRank(feedRank);
        eventPromotionRepository.save(eventPromotionToSave);
    }

    public void endEventPromotion(EventDto eventDto, LocalDateTime startDate, LocalDateTime endDate,
                                  EventPromotionType promotionType, PromotionPriority promotionPriority) {
        validateEventDto(eventDto);
        PromotionValidation.validateEventPromotion(eventDto.eventId(), startDate,
                endDate, promotionType, promotionPriority);
        int promotionViewsPercentage = promotionType.getUserPercentage();
        int feedRank = promotionPriority.getFeedRank();
        EventPromotion eventPromotion = eventPromotionRepository.findSamePromotion(
                eventDto.eventId(), startDate, endDate, promotionViewsPercentage, feedRank);
        if (eventPromotion == null) {
            String message = String.format(NO_EVENT_PROMOTION_FOUND,
                    eventDto.eventId(), startDate, endDate, promotionViewsPercentage, feedRank);
            log.error(message);
            throw new PromotionNotFoundException(message);
        }
        eventPromotionRepository.delete(eventPromotion);
    }


    public BigDecimal calculateEventPromotionPrice(Long eventId, LocalDateTime startDate, LocalDateTime endDate,
                                                   int userPercentage, int feedRank) {
        PromotionValidation.validateEventId(eventId);
        PromotionValidation.validateDates(startDate, endDate);
        long seconds = Duration.between(startDate, endDate).toSeconds();
        BigDecimal cost = EVENT_PROMOTION_PRICE_PER_MINUTE.multiply(
                BigDecimal.valueOf(seconds).divide(SECONDS_IN_MINUTE, RoundingMode.CEILING));

        cost = cost.add(EventPromotionPricing.getPrice(userPercentage, feedRank));
        Integer discountCount = eventPromotionCountRepository.findCountByEventId(eventId);
        if (discountCount == null) {
            discountCount = 0;
        }
        BigDecimal discountPercentage = MAX_DISCOUNT.min(EVENT_PROMOTION_PRICE_DECREASE.multiply(
                BigDecimal.valueOf(discountCount)));
        return cost.multiply(BigDecimal.valueOf(1.00).subtract(discountPercentage));
    }

    public BigDecimal calculateEventPromotionPriceDifferenceOnTypeChange(
            Long eventId, LocalDateTime startDate, LocalDateTime endDate,
            EventPromotionType newPromotionType, PromotionPriority promotionPriority) {

        PromotionValidation.validateEventPromotion(eventId, startDate, endDate, newPromotionType, promotionPriority);
        Integer oldUserPercentage = eventPromotionRepository.getUserPercentage(eventId, startDate, endDate,
                promotionPriority.getFeedRank());
        if (oldUserPercentage == null) {
            String message = String.format(CANT_UPDATE_EVENT_PROMOTION_TYPE,
                    eventId, startDate, endDate, promotionPriority);
            log.error(message);
            throw new PromotionNotFoundException(message);
        } else if (oldUserPercentage == newPromotionType.getUserPercentage()) {
            String message = String.format(CANT_UPDATE_EVENT_PROMOTION_TYPE + ". Such promotion already exists",
                    eventId, startDate, endDate, promotionPriority);
            log.error(message);
            throw new IllegalArgumentException(message);
        }
        return calculateEventPromotionPrice(eventId, startDate, endDate,
                newPromotionType.getUserPercentage(), promotionPriority.getFeedRank())
                .subtract(calculateEventPromotionPrice(eventId, startDate, endDate,
                        oldUserPercentage, promotionPriority.getFeedRank()));
    }

    public BigDecimal calculateEventPromotionPriceDifferenceOnPriorityChange(
            Long eventId, LocalDateTime startDate, LocalDateTime endDate,
            EventPromotionType promotionType, PromotionPriority newPromotionPriority) {

        PromotionValidation.validateEventPromotion(eventId, startDate, endDate, promotionType, newPromotionPriority);
        Integer oldFeedRank = eventPromotionRepository.getEventFeedRank(eventId, startDate, endDate,
                promotionType.getUserPercentage());
        if (oldFeedRank == null) {
            String message = String.format(CANT_UPDATE_EVENT_PROMOTION_PRIORITY,
                    eventId, startDate, endDate, promotionType);
            log.error(message);
            throw new PromotionNotFoundException(message);
        } else if (oldFeedRank == newPromotionPriority.getFeedRank()) {
            String message = String.format(CANT_UPDATE_EVENT_PROMOTION_PRIORITY + ". Such promotion already exists",
                    eventId, startDate, endDate, promotionType);
            log.error(message);
            throw new IllegalArgumentException(message);
        }
        return calculateEventPromotionPrice(eventId, startDate, endDate,
                promotionType.getUserPercentage(), newPromotionPriority.getFeedRank())
                .subtract(calculateEventPromotionPrice(eventId, startDate, endDate,
                        promotionType.getUserPercentage(), oldFeedRank));
    }

    public void updateEventPromotionCount(long eventId, BigDecimal cost) {
        Integer discountCountOld = eventPromotionCountRepository.findCountByEventId(eventId);
        int discountCountNew = cost.divide(DISCOUNT_THRESHOLD, RoundingMode.FLOOR).intValue();
        if (discountCountOld == null) {
            EventPromotionCount eventPromotionCount = new EventPromotionCount();
            eventPromotionCount.setCount(discountCountNew);
            eventPromotionCount.setEventId(eventId);
            eventPromotionCountRepository.save(eventPromotionCount);
            log.info("Saved new event promotion count. eventId: {} with count: {}", eventId, discountCountNew);
        } else {
            eventPromotionCountRepository.incrementCountByEventId(eventId, discountCountNew);
            log.info("Incremented event promotion count. eventId: {} by: {}", eventId, discountCountNew);
        }
    }

    private void validateEventDto(EventDto eventDto) {
        if (eventDto == null) {
            log.error(EVENT_DTO_CANNOT_BE_NULL);
            throw new IllegalArgumentException(EVENT_DTO_CANNOT_BE_NULL);
        }
    }
}
