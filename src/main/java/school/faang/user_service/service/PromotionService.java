package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.promotion.EventDto;
import school.faang.user_service.dto.promotion.UserDto;
import school.faang.user_service.entity.promotion.event.EventPromotion;
import school.faang.user_service.entity.promotion.event.EventPromotionCount;
import school.faang.user_service.entity.promotion.user.UserPromotion;
import school.faang.user_service.entity.promotion.user.UserPromotionCount;
import school.faang.user_service.exception.DuplicatePromotionException;
import school.faang.user_service.repository.promotion.EventPromotionCountRepository;
import school.faang.user_service.repository.promotion.EventPromotionRepository;
import school.faang.user_service.repository.promotion.UserPromotionCountRepository;
import school.faang.user_service.repository.promotion.UserPromotionRepository;
import school.faang.user_service.service.promotion.EventPromotionPricing;
import school.faang.user_service.service.promotion.EventPromotionType;
import school.faang.user_service.service.promotion.UserPromotionPricing;
import school.faang.user_service.service.promotion.UserPromotionType;
import school.faang.user_service.utils.validatonUtils.PromotionValidation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromotionService {
    private static final String DUPLICATE_USER_PROMOTION_MESSAGE = "User promotion for userId=%d with startDate=%s, endDate=%s and percentage=%d already exists in DB";
    private static final String DUPLICATE_EVENT_PROMOTION_MESSAGE = "Event promotion for eventId=%d with startDate=%s, endDate=%s and percentage=%d already exists in DB";
    private static final String NO_USER_PROMOTION_FOUND = "No promotion found for userId=%d, startDate=%s, endDate=%s and percentage=%d";
    private static final String NO_EVENT_PROMOTION_FOUND = "No promotion found for eventId=%d, startDate=%s, endDate=%s and percentage=%d";
    private static final BigDecimal USER_PROMOTION_PRICE_DECREASE = new BigDecimal("0.03");
    private static final BigDecimal EVENT_PROMOTION_PRICE_DECREASE = new BigDecimal("0.02");
    private static final BigDecimal MAX_DISCOUNT = new BigDecimal("0.20");
    private static final BigDecimal DISCOUNT_THRESHOLD = new BigDecimal("500");
    private static final BigDecimal USER_PROMOTION_PRICE_PER_MINUTE = new BigDecimal("5");
    private static final BigDecimal EVENT_PROMOTION_PRICE_PER_MINUTE = new BigDecimal("7");
    private static final BigDecimal SECONDS_IN_MINUTE = new BigDecimal("60");

    private final UserPromotionRepository userPromotionRepository;
    private final EventPromotionRepository eventPromotionRepository;
    private final UserPromotionCountRepository userPromotionCountRepository;
    private final EventPromotionCountRepository eventPromotionCountRepository;

    public void startUserPromotion(UserDto userDto, LocalDateTime startDate, LocalDateTime endDate, UserPromotionType promotionType) {
        PromotionValidation.validateUserPromotion(userDto, startDate, endDate, promotionType);
        int userPercentage = promotionType.getUserPercentage();
        UserPromotion userPromotion = userPromotionRepository.findSamePromotion(userDto.userId(), startDate, endDate, userPercentage);
        if (userPromotion != null) {
            String message = String.format(DUPLICATE_USER_PROMOTION_MESSAGE, userDto.userId(), startDate, endDate, userPercentage);
            log.error(message);
            throw new DuplicatePromotionException(message);
        }

        userPromotion = userPromotionRepository.findPromotionByUserIdStartDateEndDate(userDto.userId(), startDate, endDate);
        if (userPromotion != null) {
            userPromotionRepository.updatePromotionPercentage(userDto.userId(), startDate, endDate, userPercentage);
            log.info("Updated userPercentage in user promotion for userId={}, startDate={}, endDate={} and now equals to {} ",
                    userDto.userId(), startDate, endDate, userPercentage);
            return;
        }

        UserPromotion userPromotionToSave = new UserPromotion();
        userPromotionToSave.setUserId(userDto.userId());
        userPromotionToSave.setPercentage(userPercentage);
        userPromotionToSave.setStartDate(startDate);
        userPromotionToSave.setEndDate(endDate);
        userPromotionRepository.save(userPromotionToSave);
        log.info("User promotion for userId={} with startDate={}, endDate={} and userPercentage={} saved in DB",
                userDto.userId(), startDate, endDate, userPercentage);
    }

    public void endUserPromotion(UserDto userDto, LocalDateTime startDate, LocalDateTime endDate, UserPromotionType promotionType) {
        PromotionValidation.validateUserPromotion(userDto, startDate, endDate, promotionType);
        int userPercentage = promotionType.getUserPercentage();
        UserPromotion userPromotion = userPromotionRepository.findSamePromotion(userDto.userId(), startDate, endDate, userPercentage);
        if (userPromotion == null) {
            String message = String.format(NO_USER_PROMOTION_FOUND, userDto.userId(), startDate, endDate, userPercentage);
            log.error(message);
            throw new IllegalArgumentException(message);
        }
        userPromotionRepository.delete(userPromotion);
        log.info("User promotion for userId={} with startDate={}, endDate={} and userPercentage={} deleted from DB",
                userDto.userId(), startDate, endDate, userPercentage);
    }

    public void startEventPromotion(EventDto eventDto, LocalDateTime startDate, LocalDateTime endDate, EventPromotionType promotionType) {
        PromotionValidation.validateEventPromotion(eventDto, startDate, endDate, promotionType);
        int percentage = promotionType.getEventPercentage();
        EventPromotion eventPromotion = eventPromotionRepository.findSamePromotion(eventDto.eventId(), startDate, endDate, percentage);
        if (eventPromotion != null) {
            String message = String.format(DUPLICATE_EVENT_PROMOTION_MESSAGE, eventDto.eventId(), startDate, endDate, percentage);
            log.error(message);
            throw new DuplicatePromotionException(message);
        }

        eventPromotion = eventPromotionRepository.findPromotionByEventIdStartDateEndDate(eventDto.eventId(), startDate, endDate);
        if (eventPromotion != null) {
            eventPromotionRepository.updatePromotionPercentage(eventDto.eventId(), startDate, endDate, percentage);
            log.info("Updated userPercentage in event promotion for eventId={}, startDate={}, endDate={} and now equals to {} ",
                    eventDto.eventId(), startDate, endDate, percentage);
            return;
        }

        EventPromotion eventPromotionToSave = new EventPromotion();
        eventPromotionToSave.setEventId(eventDto.eventId());
        eventPromotionToSave.setPercentage(percentage);
        eventPromotionToSave.setStartDate(startDate);
        eventPromotionToSave.setEndDate(endDate);
        eventPromotionRepository.save(eventPromotionToSave);
        log.info("Event promotion for eventId={} with startDate={}, endDate={} and userPercentage={} started",
                eventDto.eventId(), startDate, endDate, percentage);
    }

    public void endEventPromotion(EventDto eventDto, LocalDateTime startDate, LocalDateTime endDate, EventPromotionType promotionType) {
        PromotionValidation.validateEventPromotion(eventDto, startDate, endDate, promotionType);
        int eventPercentage = promotionType.getEventPercentage();
        EventPromotion eventPromotion = eventPromotionRepository.findSamePromotion(eventDto.eventId(), startDate, endDate, eventPercentage);
        if (eventPromotion == null) {
            String message = String.format(NO_EVENT_PROMOTION_FOUND, eventDto.eventId(), startDate, endDate, eventPercentage);
            log.error(message);
            throw new IllegalArgumentException(message);
        }
        eventPromotionRepository.delete(eventPromotion);
        log.info("Event promotion for userId={} with startDate={}, endDate={} and userPercentage={} deleted from DB",
                eventPromotion.getEventId(), startDate, endDate, eventPercentage);
    }

    public BigDecimal calculateUserPromotionPrice(Long userId, LocalDateTime startDate, LocalDateTime endDate, UserPromotionType promotionType) {
        PromotionValidation.validateUserPromotionPriceCalculation(userId, startDate, endDate, promotionType);
        long seconds = Duration.between(startDate, endDate).toSeconds();
        BigDecimal cost = USER_PROMOTION_PRICE_PER_MINUTE.multiply(
                BigDecimal.valueOf(seconds).divide(SECONDS_IN_MINUTE, RoundingMode.CEILING));

        cost = cost.add(UserPromotionPricing.getPrice(promotionType));
        Integer discountCount = userPromotionCountRepository.findCountByUserId(userId);
        if (discountCount == null) {
            discountCount = 0;
        }
        BigDecimal discountPercentage = MAX_DISCOUNT.min(USER_PROMOTION_PRICE_DECREASE.multiply(BigDecimal.valueOf(discountCount)));
        return cost.multiply(BigDecimal.valueOf(1.00).subtract(discountPercentage));
    }

    public BigDecimal calculateEventPromotionPrice(Long eventId, LocalDateTime startDate, LocalDateTime endDate, EventPromotionType promotionType) {
        PromotionValidation.validateEventPromotionPriceCalculation(eventId, startDate, endDate, promotionType);
        long seconds = Duration.between(startDate, endDate).toSeconds();
        BigDecimal cost = EVENT_PROMOTION_PRICE_PER_MINUTE.multiply(
                BigDecimal.valueOf(seconds).divide(SECONDS_IN_MINUTE, RoundingMode.CEILING));

        cost = cost.add(EventPromotionPricing.getPrice(promotionType));
        Integer discountCount = userPromotionCountRepository.findCountByUserId(eventId);
        if (discountCount == null) {
            discountCount = 0;
        }
        BigDecimal discountPercentage = MAX_DISCOUNT.min(EVENT_PROMOTION_PRICE_DECREASE.multiply(BigDecimal.valueOf(discountCount)));
        return cost.multiply(BigDecimal.valueOf(1.00).subtract(discountPercentage));
    }

    public void updateUserPromotionCount(long userId, BigDecimal cost) {
        Integer discountCountOld = userPromotionCountRepository.findCountByUserId(userId);
        int discountCountNew = cost.divide(DISCOUNT_THRESHOLD, RoundingMode.FLOOR).intValue();
        if (discountCountOld == null) {
            UserPromotionCount userPromotionCount = new UserPromotionCount();
            userPromotionCount.setCount(discountCountNew);
            userPromotionCount.setUserId(userId);
            userPromotionCountRepository.save(userPromotionCount);
            log.info("Saved new user promotion count. userId: {} with count: {}", userId, discountCountNew);
        } else {
            userPromotionCountRepository.incrementCountByUserId(userId, discountCountNew);
            log.info("Incremented user promotion count. userId: {} by: {}", userId, discountCountNew);
        }
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
}
