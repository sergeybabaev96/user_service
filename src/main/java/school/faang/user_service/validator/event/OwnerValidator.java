package school.faang.user_service.validator.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.validator.EventValidator;

@Component
public class OwnerValidator implements EventValidator {
    @Override
    public boolean validate(EventDto event) {
        return event.getOwnerId() != null && event.getOwnerId() > 0;
    }
}
