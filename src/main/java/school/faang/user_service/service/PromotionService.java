package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.promotion.EventDto;
import school.faang.user_service.dto.promotion.UserDto;
import school.faang.user_service.entity.promotion.event.EventPromotion;
import school.faang.user_service.entity.promotion.event.EventPromotionType;
import school.faang.user_service.entity.promotion.user.UserPromotion;
import school.faang.user_service.entity.promotion.user.UserPromotionType;
import school.faang.user_service.exception.DuplicatePromotionException;
import school.faang.user_service.repository.promotion.EventPromotionRepository;
import school.faang.user_service.repository.promotion.UserPromotionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromotionService {
    private static final String DUPLICATE_USER_PROMOTION_MESSAGE = "User promotion for userId=%d with startDate=%s, endDate=%s and percentage=%d already exists in DB";
    private static final String DUPLICATE_EVENT_PROMOTION_MESSAGE = "Event promotion for eventId=%d with startDate=%s, endDate=%s and percentage=%d already exists in DB";
    private static final String USER_PROMOTION_MESSAGE = "Promotion for userId={} with startDate={}, endDate={} and percentage={}";
    private static final String EVENT_PROMOTION_MESSAGE = "Promotion for EventId={} with startDate={} and endDate={} at percentage={}";
    private static final String NO_USER_PROMOTION_FOUND = "No promotion found for userId=%d, startDate=%s, endDate=%s and percentage=%d";
    private static final String NO_EVENT_PROMOTION_FOUND = "No promotion found for eventId=%d, startDate=%s, endDate=%s and percentage=%d";

    private final UserPromotionRepository userPromotionRepository;
    private final EventPromotionRepository eventPromotionRepository;

    public void startUserPromotion(UserDto userDto, LocalDateTime startDate, LocalDateTime endDate, UserPromotionType promotionType) {
        int percentage = promotionType.getPercentage();
        UserPromotion userPromotion = userPromotionRepository.findSamePromotion(
                userDto.userId(), startDate, endDate, percentage);
        if (userPromotion != null) {
            String message = String.format(DUPLICATE_USER_PROMOTION_MESSAGE, userDto.userId(), startDate, endDate, percentage);
            log.error(message);
            throw new DuplicatePromotionException(message);
        }

        userPromotion = userPromotionRepository.findPromotionByUserIdStartDateEndDate(
                userDto.userId(), startDate, endDate);
        if (userPromotion != null) {
            userPromotionRepository.updatePromotionPercentage(userDto.userId(), startDate, endDate, percentage);
            log.info("User promotion updated. " + USER_PROMOTION_MESSAGE, userDto.userId(), startDate, endDate, percentage);
            return;
        }
        UserPromotion userPromotionToSave = new UserPromotion();
        userPromotionToSave.setUserId(userDto.userId());
        userPromotionToSave.setPercentage(percentage);
        userPromotionToSave.setStartDate(startDate);
        userPromotionToSave.setEndDate(endDate);
        userPromotionRepository.save(userPromotionToSave);
        log.info(USER_PROMOTION_MESSAGE + " created", userDto.userId(), startDate, endDate, percentage);
    }

    public void endUserPromotion(UserDto userDto, LocalDateTime startDate, LocalDateTime endDate, UserPromotionType promotionType) {
        int percentage = promotionType.getPercentage();
        UserPromotion userPromotion = userPromotionRepository.findSamePromotion(
                userDto.userId(), startDate, endDate, percentage);
        if (userPromotion == null) {
            String message = String.format(NO_USER_PROMOTION_FOUND, userDto.userId(), startDate, endDate, percentage);
            log.error(message);
            throw new IllegalArgumentException(message);
        }
        userPromotionRepository.delete(userPromotion);
    }

    public void startEventPromotion(EventDto eventDto, LocalDateTime startDate, LocalDateTime endDate, EventPromotionType promotionType) {
        int percentage = promotionType.getPercentage();
        EventPromotion eventPromotion = eventPromotionRepository.findSamePromotion(
                eventDto.eventId(), startDate, endDate, percentage);
        if (eventPromotion != null) {
            String message = String.format(DUPLICATE_EVENT_PROMOTION_MESSAGE, eventDto.eventId(), startDate, endDate, percentage);
            log.error(message);
            throw new DuplicatePromotionException(message);
        }

        eventPromotion = eventPromotionRepository.findPromotionByEventIdStartDateEndDate(
                eventDto.eventId(), startDate, endDate);
        if (eventPromotion != null) {
            eventPromotionRepository.updatePromotionPercentage(eventDto.eventId(), startDate, endDate, percentage);
            log.info("Event promotion updated." + EVENT_PROMOTION_MESSAGE + " is active now",
                    eventDto.eventId(), startDate, endDate, percentage);
            return;
        }
        EventPromotion eventPromotionToSave = new EventPromotion();
        eventPromotionToSave.setEventId(eventDto.eventId());
        eventPromotionToSave.setPercentage(percentage);
        eventPromotionToSave.setStartDate(startDate);
        eventPromotionToSave.setEndDate(endDate);
        eventPromotionRepository.save(eventPromotionToSave);
        log.info(EVENT_PROMOTION_MESSAGE + " created", eventDto.eventId(), startDate, endDate, percentage);
    }

    public void endEventPromotion(EventDto eventDto, LocalDateTime startDate, LocalDateTime endDate, EventPromotionType promotionType) {
        int percentage = promotionType.getPercentage();
        EventPromotion eventPromotion = eventPromotionRepository.findSamePromotion(
                eventDto.eventId(), startDate, endDate, percentage);
        if (eventPromotion == null) {
            String message = String.format(NO_EVENT_PROMOTION_FOUND, eventDto.eventId(), startDate, endDate, percentage);
            log.error(message);
            throw new IllegalArgumentException(message);
        }
        eventPromotionRepository.delete(eventPromotion);
    }

    public BigDecimal calculateUserPromotionPrice(Long userId, LocalDateTime startDate, LocalDateTime endDate, UserPromotionType promotionType) {

    }

    public BigDecimal calculateEventPromotionPrice(Long eventId, LocalDateTime startDate, LocalDateTime endDate, EventPromotionType promotionType) {

    }
}
