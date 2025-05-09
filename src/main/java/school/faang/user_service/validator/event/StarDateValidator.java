package school.faang.user_service.validator.event;

import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.validator.EventValidator;

@Controller
public class StarDateValidator implements EventValidator {
    @Override
    public boolean validate(EventDto event) {
        return event.getStartDate() != null;
    }
}
