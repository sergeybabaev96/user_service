package school.faang.user_service.utility.validator.event.dto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import school.faang.user_service.dto.event.EventDto;

public class EventDatesValidator implements ConstraintValidator<ValidEventDates, EventDto> {

    @Override
    public boolean isValid(EventDto eventDto, ConstraintValidatorContext context) {
        if (eventDto == null) {
            return true;
        }
        return eventDto.endDate().isAfter(eventDto.startDate());
    }
}
