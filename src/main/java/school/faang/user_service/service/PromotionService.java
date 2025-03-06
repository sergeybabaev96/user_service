package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.promotion.EventDto;
import school.faang.user_service.dto.promotion.UserDto;
import school.faang.user_service.entity.promotion.EventPromotion;
import school.faang.user_service.entity.promotion.UserPromotion;
import school.faang.user_service.exception.DuplicatePromotionException;
import school.faang.user_service.repository.promotion.EventPromotionRepository;
import school.faang.user_service.repository.promotion.UserPromotionRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromotionService {
    private static final String DUPLICATE_USER_PROMOTION_MESSAGE = "User promotion for userId=%d with startDate=%s and endDate=%s at percentage=%d already exists in DB";
    private static final String DUPLICATE_EVENT_PROMOTION_MESSAGE = "Event promotion for userId=%d with startDate=%s and endDate=%s at percentage=%d already exists in DB";

    private final UserPromotionRepository userPromotionRepository;
    private final EventPromotionRepository eventPromotionRepository;

    public void startUserPromotion(UserDto userDto, LocalDateTime startDate, LocalDateTime endDate, int percentage) {
        UserPromotion userPromotion = userPromotionRepository.findSamePromotion(
                userDto.userId(), startDate, endDate, percentage);
        if (userPromotion != null) {
            String message = String.format(DUPLICATE_USER_PROMOTION_MESSAGE, userDto.userId(), startDate, endDate, percentage);
            log.warn(message);
            throw new DuplicatePromotionException(message);
        }

        userPromotion = userPromotionRepository.findPromotionByIdStartDateEndDate(
                userDto.userId(), startDate, endDate);
        if (userPromotion != null) {
            userPromotionRepository.updatePromotionPercentage(
                    userDto.userId(), startDate, endDate, percentage);
            log.info("User promotion updated. Promotion for userId={} with startDate={} and endDate={} at percentage={} is active now",
                    userDto.userId(), startDate, endDate, percentage);
        } else {
            UserPromotion userPromotionToSave = new UserPromotion();
            userPromotionToSave.setUserId(userDto.userId());
            userPromotionToSave.setPercentage(percentage);
            userPromotionToSave.setStartDate(startDate);
            userPromotionToSave.setEndDate(endDate);
            userPromotionRepository.save(userPromotionToSave);
            log.info("User promotion for userId={} with startDate={} and endDate={} at percentage={} created",
                    userDto.userId(), startDate, endDate, percentage);
        }
    }

    public void endUserPromotion(UserDto userDto, LocalDateTime startDate, LocalDateTime endDate, int percentage) {

    }

    public void startEventPromotion(EventDto eventDto, LocalDateTime startDate, LocalDateTime endDate, int percentage) {
        EventPromotion eventPromotion = eventPromotionRepository.findSamePromotion(
                eventDto.eventId(), startDate, endDate, percentage);
        if (eventPromotion != null) {
            String message = String.format(DUPLICATE_EVENT_PROMOTION_MESSAGE, eventDto.eventId(), startDate, endDate, percentage);
            log.warn(message);
            throw new DuplicatePromotionException(message);
        }

        eventPromotion = eventPromotionRepository.findPromotionByIdStartDateEndDate(
                eventDto.eventId(), startDate, endDate);
        if (eventPromotion != null) {
            eventPromotionRepository.updatePromotionPercentage(
                    eventDto.eventId(), startDate, endDate, percentage);
            log.info("Event promotion updated. Promotion for userId={} with startDate={} and endDate={} at percentage={} is active now",
                    eventDto.eventId(), startDate, endDate, percentage);
        } else {
            EventPromotion eventPromotionToSave = new EventPromotion();
            eventPromotionToSave.setEventId(eventDto.eventId());
            eventPromotionToSave.setPercentage(percentage);
            eventPromotionToSave.setStartDate(startDate);
            eventPromotionToSave.setEndDate(endDate);
            eventPromotionRepository.save(eventPromotionToSave);
            log.info("Event promotion for userId={} with startDate={} and endDate={} at percentage={} created",
                    eventDto.eventId(), startDate, endDate, percentage);
        }
    }

    public void endEventPromotion(EventDto eventDto, LocalDateTime startDate, LocalDateTime endDate, int percentage) {

    }
}
