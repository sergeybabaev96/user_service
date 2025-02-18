package school.faang.user_service.utility.validator.event.dto;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = EventDatesValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEventDates {
    String message() default "End date must be after start date!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
