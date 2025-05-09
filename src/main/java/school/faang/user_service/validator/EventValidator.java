package school.faang.user_service.validator;

import school.faang.user_service.dto.event.EventDto;

public interface EventValidator {
    boolean validate(EventDto event);
}
