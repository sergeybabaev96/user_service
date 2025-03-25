package school.faang.user_service.utils;

import lombok.experimental.UtilityClass;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;

import java.time.LocalDateTime;
import java.util.Objects;

@UtilityClass
public class ValidationUtils {
    public static void validateEvent(EventDto event) {
        if (event == null || event.title().isBlank() || !Objects.nonNull(event.startDate())
                || event.startDate().isBefore(LocalDateTime.now())
                || event.ownerId() == null ) {
            throw new DataValidationException("Event not confirmed");
        }
    }

    public static void validateEventId(Long id) {
        if(id == null || id <= 0) {
            throw new DataValidationException("Event id not valid");
        }
    }
}
