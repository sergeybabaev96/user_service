package school.faang.user_service.utility.validator;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.exception.DataValidationException;

class UserDtoValidatorTest {
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    UserDtoValidator userDtoValidator = new UserDtoValidator(validator);
    UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .id(1L)
                .username("username")
                .email("user@gmail.com")
                .phone("54361")
                .aboutMe("blablabla")
                .country("Russia")
                .city("Moscow")
                .experience(2)
                .createdAt("20.20.20")
                .build();
    }

    @Test
    void validate() {
        Assertions.assertTrue(userDtoValidator.validate(userDto));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "122"})
    void testEmailEmptyIncorrect(String arg){
        userDto.setEmail(arg);
        String exceptionMessage = "email is not correct";
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