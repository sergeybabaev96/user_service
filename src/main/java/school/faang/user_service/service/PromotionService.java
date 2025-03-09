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
import school.faang.user_service.exception.PromotionNotFoundException;
import school.faang.user_service.model.promotion.PromotionPriority;
import school.faang.user_service.model.promotion.event.EventPromotionPricing;
import school.faang.user_service.model.promotion.event.EventPromotionType;
import school.faang.user_service.model.promotion.user.UserPromotionPricing;
import school.faang.user_service.model.promotion.user.UserPromotionType;
import school.faang.user_service.repository.promotion.EventPromotionCountRepository;
import school.faang.user_service.repository.promotion.EventPromotionRepository;
import school.faang.user_service.repository.promotion.UserPromotionCountRepository;
import school.faang.user_service.repository.promotion.UserPromotionRepository;
import school.faang.user_service.utils.validatonUtils.PromotionValidation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromotionService {
    public static final String USER_DTO_CANNOT_BE_NULL = "User DTO can't be null";
    public static final String EVENT_DTO_CANNOT_BE_NULL = "Event DTO can't be null";
    public static final String DUPLICATE_USER_PROMOTION_MESSAGE = "User promotion for userId=%d with " +
            "startDate=%s, endDate=%s already exists in DB";
    public static final String DUPLICATE_EVENT_PROMOTION_MESSAGE = "Event promotion for eventId=%d with" +
            " startDate=%s, endDate=%s already exists in DB";
    public static final String NO_USER_PROMOTION_FOUND = "No promotion found for userId=%d, startDate=%s," +
            " endDate=%s, userPercentage=%d and feedRank=%d";
    public static final String NO_EVENT_PROMOTION_FOUND = "No promotion found for eventId=%d, startDate=%s," +
            " endDate=%s, userPercentage=%d and feedRank=%d";
    private static final String CANT_UPDATE_USER_PROMOTION_TYPE = "Can't update promotionType for userId=%d, " +
            "startDate=%s, endDate=%s and promotionPriority=%s.";
    private static final String CANT_UPDATE_EVENT_PROMOTION_TYPE = "Can't update promotionType for eventId=%d," +
            " startDate=%s, endDate=%s and promotionPriority=%s.";
    private static final String CANT_UPDATE_USER_PROMOTION_PRIORITY = "Can't update promotionPriority for " +
            "userId=%d, startDate=%s, endDate=%s and promotionType=%s.";
    private static final String CANT_UPDATE_EVENT_PROMOTION_PRIORITY = "Can't update promotionPriority for " +
            "eventId=%d, startDate=%s, endDate=%s and promotionType=%s.";

    private static final BigDecimal USER_PROMOTION_PRICE_DECREASE = new BigDecimal("0.03");
    private static final BigDecimal EVENT_PROMOTION_PRICE_DECREASE = new BigDecimal("0.02");
    private static final BigDecimal MAX_DISCOUNT = new BigDecimal("0.20");
    private static final BigDecimal DISCOUNT_THRESHOLD = new BigDecimal("50000");
    private static final BigDecimal USER_PROMOTION_PRICE_PER_MINUTE = new BigDecimal("5");
    private static final BigDecimal EVENT_PROMOTION_PRICE_PER_MINUTE = new BigDecimal("7");
    private static final BigDecimal SECONDS_IN_MINUTE = new BigDecimal("60");

    private final UserPromotionRepository userPromotionRepository;
    private final EventPromotionRepository eventPromotionRepository;
    private final UserPromotionCountRepository userPromotionCountRepository;
    private final EventPromotionCountRepository eventPromotionCountRepository;

    public void startUserPromotion(UserDto userDto, LocalDateTime startDate, LocalDateTime endDate,
                                   UserPromotionType promotionType, PromotionPriority promotionPriority) {
        validateUserDto(userDto);
        PromotionValidation.validateUserPromotion(userDto.userId(), startDate,
                endDate, promotionType, promotionPriority);
        int promotionViewsPercentage = promotionType.getUserPercentage();
        int feedRank = promotionPriority.getFeedRank();

        UserPromotion userPromotion = userPromotionRepository.findPromotionByUserIdStartDateEndDate(
                userDto.userId(), startDate, endDate);
        if (userPromotion != null) {
            String message = String.format(DUPLICATE_USER_PROMOTION_MESSAGE, userDto.userId(), startDate, endDate);
            log.error(message);
            throw new DuplicatePromotionException(message);
        }

        UserPromotion userPromotionToSave = new UserPromotion();
        userPromotionToSave.setUserId(userDto.userId());
        userPromotionToSave.setPercentage(promotionViewsPercentage);
        userPromotionToSave.setStartDate(startDate);
        userPromotionToSave.setEndDate(endDate);
        userPromotionToSave.setFeedRank(feedRank);
        userPromotionRepository.save(userPromotionToSave);
    }

    public void endUserPromotion(UserDto userDto, LocalDateTime startDate, LocalDateTime endDate,
                                 UserPromotionType promotionType, PromotionPriority promotionPriority) {
        validateUserDto(userDto);
        PromotionValidation.validateUserPromotion(userDto.userId(), startDate,
                endDate, promotionType, promotionPriority);
        int promotionViewsPercentage = promotionType.getUserPercentage();
        int feedRank = promotionPriority.getFeedRank();
        UserPromotion userPromotion = userPromotionRepository.findSamePromotion(
                userDto.userId(), startDate, endDate, promotionViewsPercentage, feedRank);

        if (userPromotion == null) {
            String message = String.format(NO_USER_PROMOTION_FOUND,
                    userDto.userId(), startDate, endDate, promotionViewsPercentage, feedRank);
            log.error(message);
            throw new PromotionNotFoundException(message);
        }
        userPromotionRepository.delete(userPromotion);
    }

    public void startEventPromotion(EventDto eventDto, LocalDateTime startDate, LocalDateTime endDate,
                                    EventPromotionType promotionType, PromotionPriority promotionPriority) {
        validateEventDto(eventDto);
        PromotionValidation.validateEventPromotion(eventDto.eventId(), startDate,
                endDate, promotionType, promotionPriority);
        int promotionViewsPercentage = promotionType.getEventPercentage();
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
        int promotionViewsPercentage = promotionType.getEventPercentage();
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

    public void updateUserPromotionType(Long userId, LocalDateTime startDate, LocalDateTime endDate,
                                        UserPromotionType newPromotionType, PromotionPriority promotionPriority) {
        userPromotionRepository.updateUserPromotionPercentage(userId, startDate, endDate,
                newPromotionType.getUserPercentage(), promotionPriority.getFeedRank());
    }

    public void updateEventPromotionType(Long eventId, LocalDateTime startDate, LocalDateTime endDate,
                                         UserPromotionType newPromotionType, PromotionPriority promotionPriority) {
        eventPromotionRepository.updatePromotionPercentage(eventId, startDate, endDate,
                newPromotionType.getUserPercentage(), promotionPriority.getFeedRank());
    }


    public void updateUserPromotionPriority(Long userId, LocalDateTime startDate, LocalDateTime endDate,
                                            UserPromotionType promotionType, PromotionPriority newPromotionPriority) {
        userPromotionRepository.updateUserFeedRank(userId, startDate, endDate,
                promotionType.getUserPercentage(), newPromotionPriority.getFeedRank());
    }

    public void updateEventPromotionPriority(Long userId, LocalDateTime startDate, LocalDateTime endDate,
                                             UserPromotionType promotionType, PromotionPriority newPromotionPriority) {
        eventPromotionRepository.updateEventFeedRank(userId, startDate, endDate,
                promotionType.getUserPercentage(), newPromotionPriority.getFeedRank());
    }

    public BigDecimal calculateUserPromotionPrice(Long userId, LocalDateTime startDate, LocalDateTime endDate,
                                                  int userPercentage, int feedRank) {
        PromotionValidation.validateUserId(userId);
        PromotionValidation.validateDates(startDate, endDate);
        long seconds = Duration.between(startDate, endDate).toSeconds();
        BigDecimal cost = USER_PROMOTION_PRICE_PER_MINUTE.multiply(
                BigDecimal.valueOf(seconds).divide(SECONDS_IN_MINUTE, RoundingMode.CEILING));

        cost = cost.add(UserPromotionPricing.getPrice(userPercentage, feedRank));
        Integer discountCount = userPromotionCountRepository.findCountByUserId(userId);
        if (discountCount == null) {
            discountCount = 0;
        }
        BigDecimal discountPercentage = MAX_DISCOUNT.min(USER_PROMOTION_PRICE_DECREASE.multiply(
                BigDecimal.valueOf(discountCount)));
        return cost.multiply(BigDecimal.valueOf(1.00).subtract(discountPercentage));
    }

    public BigDecimal calculateEventPromotionPrice(Long eventId, LocalDateTime startDate, LocalDateTime endDate,
                                                   int userPercentage, int feedRank) {
        PromotionValidation.validateEventId(eventId);
        PromotionValidation.validateDates(startDate, endDate);
        long seconds = Duration.between(startDate, endDate).toSeconds();
        BigDecimal cost = EVENT_PROMOTION_PRICE_PER_MINUTE.multiply(
                BigDecimal.valueOf(seconds).divide(SECONDS_IN_MINUTE, RoundingMode.CEILING));

        cost = cost.add(EventPromotionPricing.getPrice(userPercentage, feedRank));
        Integer discountCount = userPromotionCountRepository.findCountByUserId(eventId);
        if (discountCount == null) {
            discountCount = 0;
        }
        BigDecimal discountPercentage = MAX_DISCOUNT.min(EVENT_PROMOTION_PRICE_DECREASE.multiply(
                BigDecimal.valueOf(discountCount)));
        return cost.multiply(BigDecimal.valueOf(1.00).subtract(discountPercentage));
    }

    public BigDecimal calculateUserPromotionPriceDifferenceOnTypeChange(
            Long userId, LocalDateTime startDate, LocalDateTime endDate,
            UserPromotionType newPromotionType, PromotionPriority promotionPriority) {

        PromotionValidation.validateUserPromotion(userId, startDate, endDate, newPromotionType, promotionPriority);
        Integer oldUserPercentage = userPromotionRepository.getUserPercentage(userId, startDate, endDate,
                promotionPriority.getFeedRank());
        if (oldUserPercentage == null) {
            String message = String.format(CANT_UPDATE_USER_PROMOTION_TYPE,
                    userId, startDate, endDate, promotionPriority);
            log.error(message);
            throw new PromotionNotFoundException(message);
        } else if (oldUserPercentage == newPromotionType.getUserPercentage()) {
            String message = String.format(CANT_UPDATE_USER_PROMOTION_TYPE + ". Such promotion already exists",
                    userId, startDate, endDate, promotionPriority);
            log.error(message);
            throw new IllegalArgumentException(message);
        }
        return calculateUserPromotionPrice(userId, startDate, endDate,
                newPromotionType.getUserPercentage(), promotionPriority.getFeedRank())
                .subtract(calculateUserPromotionPrice(userId, startDate, endDate,
                        oldUserPercentage, promotionPriority.getFeedRank()));
    }

    public BigDecimal calculateEventPromotionPriceDifferenceOnTypeChange(
            Long eventId, LocalDateTime startDate, LocalDateTime endDate,
            UserPromotionType newPromotionType, PromotionPriority promotionPriority) {

        PromotionValidation.validateUserPromotion(eventId, startDate, endDate, newPromotionType, promotionPriority);
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

    public BigDecimal calculateUserPromotionPriceDifferenceOnPriorityChange(
            Long userId, LocalDateTime startDate, LocalDateTime endDate,
            UserPromotionType promotionType, PromotionPriority newPromotionPriority) {

        PromotionValidation.validateUserPromotion(userId, startDate, endDate, promotionType, newPromotionPriority);
        Integer oldFeedRank = userPromotionRepository.getUserFeedRank(userId, startDate, endDate,
                promotionType.getUserPercentage());
        if (oldFeedRank == null) {
            String message = String.format(CANT_UPDATE_USER_PROMOTION_PRIORITY,
                    userId, startDate, endDate, promotionType);
            log.error(message);
            throw new PromotionNotFoundException(message);
        } else if (oldFeedRank == newPromotionPriority.getFeedRank()) {
            String message = String.format(CANT_UPDATE_USER_PROMOTION_PRIORITY + ". Such promotion already exists",
                    userId, startDate, endDate, promotionType);
            log.error(message);
            throw new IllegalArgumentException(message);
        }
        return calculateUserPromotionPrice(userId, startDate, endDate,
                promotionType.getUserPercentage(), newPromotionPriority.getFeedRank())
                .subtract(calculateUserPromotionPrice(userId, startDate, endDate,
                        promotionType.getUserPercentage(), oldFeedRank));
    }


    public BigDecimal calculateEventPromotionPriceDifferenceOnPriorityChange(
            Long eventId, LocalDateTime startDate, LocalDateTime endDate,
            UserPromotionType promotionType, PromotionPriority newPromotionPriority) {

        PromotionValidation.validateUserPromotion(eventId, startDate, endDate, promotionType, newPromotionPriority);
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

    private void validateUserDto(UserDto userDto) {
        if (userDto == null) {
            log.error(USER_DTO_CANNOT_BE_NULL);
            throw new IllegalArgumentException(USER_DTO_CANNOT_BE_NULL);
        }
    }

    private void validateEventDto(EventDto eventDto) {
        if (eventDto == null) {
            log.error(EVENT_DTO_CANNOT_BE_NULL);
            throw new IllegalArgumentException(EVENT_DTO_CANNOT_BE_NULL);
        }
    }
}
