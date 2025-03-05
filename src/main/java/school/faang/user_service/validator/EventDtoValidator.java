package school.faang.user_service.validator;

import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;

import java.time.LocalDateTime;

public class EventDtoValidator {
    public static void validate(EventDto eventDto) {
        if (eventDto.getTitle() == null || eventDto.getTitle().isBlank()) {
            throw new DataValidationException("Title can't be null or empty.");
        }
        if (eventDto.getStartDate() == null || eventDto.getStartDate().isBefore(LocalDateTime.now())) {
            throw new DataValidationException("StartDate can't be null or in the past.");
        }
        if (eventDto.getOwnerId() == null) {
            throw new DataValidationException("OwnerId can't be null.");
        }
    }
}