package school.faang.user_service.utility.validator;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.exception.DataValidationException;

class UserDtoValidatorTest {
    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private UserDtoValidator userDtoValidator = new UserDtoValidator(validator);
    private UserDto userDto;

    @BeforeEach
    void beforeAll() {
        userDto = UserDto.builder()
                .id(1L)
                .username("user")
                .email("user@mail.com")
                .build();
    }

    @Test
    void validate() {
        Assertions.assertTrue(userDtoValidator.validate(userDto));
    }

    @Test
    void validateEmailIsEmpty() {
        userDto.setEmail("");
        String exceptionMessage = "email is not correct";
        Assertions.assertThrows(DataValidationException.class, () -> userDtoValidator.validate(userDto), exceptionMessage);
    }

    @Test
    void validateEmailIsNotCorrect() {
        userDto.setEmail("usermail.com");
        String exceptionMessage = "must be a well-formed email address";
        Assertions.assertThrows(DataValidationException.class, () -> userDtoValidator.validate(userDto), exceptionMessage);
    }

    @Test
    void validateEmailIsNull() {
        userDto.setEmail(null);
        String exceptionMessage = "email must not be null";
        Assertions.assertThrows(DataValidationException.class, () -> userDtoValidator.validate(userDto), exceptionMessage);
    }

    @Test
    void validateUserNameIsEmpty() {
        userDto.setUsername("");
        String exceptionMessage = "username must not be empty";
        Assertions.assertThrows(DataValidationException.class, () -> userDtoValidator.validate(userDto), exceptionMessage);
    }

    @Test
    void validateUserNameIsNull() {
        userDto.setUsername(null);
        String exceptionMessage = "username must not be null";
        Assertions.assertThrows(DataValidationException.class, () -> userDtoValidator.validate(userDto), exceptionMessage);
    }
}