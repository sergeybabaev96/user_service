package school.faang.user_service.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;

import java.time.LocalDateTime;

@Slf4j
@Component
public class EventDtoValidator {
    private static final String ERROR_TITLE_NULL_OR_EMPTY = "Validation failed: Title can't be null or empty";
    private static final String ERROR_START_DATE_INVALID = "Validation failed: StartDate can't be null or in the past";
    private static final String ERROR_OWNER_ID_NULL = "Validation failed: OwnerId can't be null";
    private static final String ERROR_EVENT_NULL = "Validation failed: EventDto is null";


    public static void validate(EventDto eventDto) {
        LocalDateTime currentTime = LocalDateTime.now();

        if (eventDto == null) {
            log.error(ERROR_EVENT_NULL);
            throw new DataValidationException("EventDto can't be null.");
        }

        if (eventDto.getTitle() == null || eventDto.getTitle().isBlank()) {
            log.error(ERROR_TITLE_NULL_OR_EMPTY);
            throw new DataValidationException(ERROR_TITLE_NULL_OR_EMPTY);
        }

        if (eventDto.getStartDate() == null || eventDto.getStartDate().isBefore(currentTime)) {
            log.error(ERROR_START_DATE_INVALID);
            throw new DataValidationException(ERROR_START_DATE_INVALID);
        }

        if (eventDto.getOwnerId() == null) {
            log.error(ERROR_OWNER_ID_NULL);
            throw new DataValidationException(ERROR_OWNER_ID_NULL);
        }
    }

}