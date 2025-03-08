package school.faang.user_service.utils.validatonUtils;

import lombok.extern.slf4j.Slf4j;
import school.faang.user_service.dto.promotion.EventDto;
import school.faang.user_service.dto.promotion.UserDto;
import school.faang.user_service.service.promotion.event.EventPromotionType;
import school.faang.user_service.service.promotion.user.UserPromotionType;

import java.time.LocalDateTime;

@Slf4j
public class PromotionValidation {
    private static final String USER_DTO_CANNOT_BE_NULL = "User DTO can't be null";
    private static final String EVENT_DTO_CANNOT_BE_NULL = "Event DTO can't be null";
    private static final String DATE_CANNOT_BE_NULL = "Start date and end date cannot be null";
    private static final String START_DATE_CANNOT_BE_AFTER_END_DATE = "Start date cannot be after end date";
    private static final String USER_ID_CANNOT_BE_NULL = "UserId can't be null";
    private static final String EVENT_ID_CANNOT_BE_NULL = "EventId can't be null";
    private static final String USER_PROMOTION_TYPE_CANNOT_BE_NULL = "UserPromotionType can't be null";
    private static final String EVENT_PROMOTION_TYPE_CANNOT_BE_NULL = "EventPromotionType can't be null";

    private static void validateDates(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            log.error(DATE_CANNOT_BE_NULL);
            throw new IllegalArgumentException(DATE_CANNOT_BE_NULL);
        }
        if (startDate.isAfter(endDate)) {
            log.error(START_DATE_CANNOT_BE_AFTER_END_DATE);
            throw new IllegalArgumentException(START_DATE_CANNOT_BE_AFTER_END_DATE);
        }
    }

    private static void validateUserId(Long userId) {
        if (userId == null) {
            log.error(USER_ID_CANNOT_BE_NULL);
            throw new IllegalArgumentException(USER_ID_CANNOT_BE_NULL);
        }
    }

    private static void validateEventId(Long eventId) {
        if (eventId == null) {
            log.error(EVENT_ID_CANNOT_BE_NULL);
            throw new IllegalArgumentException(EVENT_ID_CANNOT_BE_NULL);
        }
    }

    private static void validateUserPromotionType(UserPromotionType userPromotionType) {
        if (userPromotionType == null) {
            log.error(USER_PROMOTION_TYPE_CANNOT_BE_NULL);
            throw new IllegalArgumentException(USER_PROMOTION_TYPE_CANNOT_BE_NULL);
        }
    }

    private static void validateEventPromotionType(EventPromotionType userPromotionType) {
        if (userPromotionType == null) {
            log.error(EVENT_PROMOTION_TYPE_CANNOT_BE_NULL);
            throw new IllegalArgumentException(EVENT_PROMOTION_TYPE_CANNOT_BE_NULL);
        }
    }

    public static void validateUserPromotion(UserDto userDto, LocalDateTime startDate, LocalDateTime endDate, UserPromotionType promotionType) {
        if (userDto == null) {
            log.error(USER_DTO_CANNOT_BE_NULL);
            throw new IllegalArgumentException(USER_DTO_CANNOT_BE_NULL);
        }
        validateUserId(userDto.userId());
        validateDates(startDate, endDate);
        validateUserPromotionType(promotionType);
    }

    public static void validateEventPromotion(EventDto eventDto, LocalDateTime startDate, LocalDateTime endDate, EventPromotionType promotionType) {
        if (eventDto == null) {
            log.error(EVENT_DTO_CANNOT_BE_NULL);
            throw new IllegalArgumentException(EVENT_DTO_CANNOT_BE_NULL);
        }
        validateEventId(eventDto.eventId());
        validateDates(startDate, endDate);
        validateEventPromotionType(promotionType);
    }

    public static void validateUserPromotionPriceCalculation(Long userId, LocalDateTime startDate, LocalDateTime endDate, UserPromotionType promotionType) {
        validateDates(startDate, endDate);
        validateUserId(userId);
        validateUserPromotionType(promotionType);
    }

    public static void validateEventPromotionPriceCalculation(Long eventId, LocalDateTime startDate, LocalDateTime endDate, EventPromotionType promotionType) {
        validateDates(startDate, endDate);
        validateEventId(eventId);
        validateEventPromotionType(promotionType);
    }
}
