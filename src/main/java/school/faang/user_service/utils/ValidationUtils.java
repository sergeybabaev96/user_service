package school.faang.user_service.utils;

import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;

import java.time.LocalDateTime;
import java.util.Objects;

public class ValidationUtils {
    public static void validateEvent(EventDto event) {
        if (event == null || event.getTitle().isBlank() || !Objects.nonNull(event.getStartDate())
                || event.getStartDate().isBefore(LocalDateTime.now())
                || event.getOwnerId() == null ) {
            throw new DataValidationException("Event not confirmed");
        }
    }

    public static void validateEventId(Long id) {
        if(id == null || id <= 0) {
            throw new DataValidationException("Event id not valid");
        }
    }
}
