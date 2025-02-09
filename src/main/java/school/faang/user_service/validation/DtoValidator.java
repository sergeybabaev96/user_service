package school.faang.user_service.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class DtoValidator<T> {

    private final Validator validator;

    public void validate(T dto) {
        Set<ConstraintViolation<T>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            throw new DataValidationException("Validation failed: " + violations);
        }
    }

    public void validate(List<T> listDto) {
        listDto.forEach(this::validate);
    }
}
