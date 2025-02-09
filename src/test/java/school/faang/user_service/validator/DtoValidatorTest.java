package school.faang.user_service.validator;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.SubscriptionUserDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.validation.DtoValidator;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class DtoValidatorTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();
    private final DtoValidator<SubscriptionUserDto> dtoValidator = new DtoValidator<>(validator);

    @Test
    public void testValidateOnInvalidDto() {
        SubscriptionUserDto invalidDto = new SubscriptionUserDto();
        invalidDto.setId(null);
        invalidDto.setUsername("");
        invalidDto.setEmail("invalid");

        assertThrows(DataValidationException.class, () -> dtoValidator.validate(invalidDto));
    }
}