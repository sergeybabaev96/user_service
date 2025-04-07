package school.faang.user_service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.CreateUserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreateUserValidatorTest {

    public static final String TEST_USERNAME = "Dummy username";
    public static final String TEST_EMAIL = "example@mail.com";
    public static final String TEST_PASSWORD = "Dummy password";
    public static final String TEST_COUNTRY_TITLE = "Dummy country title";

    @Mock
    private UserRepository userRepository;

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private CreateUserValidator createUserValidator;

    @Test
    public void testValidateUsername_UsernameIsAlreadyExisted_Throws() {
        var username = "Already existed username";
        var requestDto = new CreateUserDto(username, TEST_EMAIL, TEST_PASSWORD, TEST_COUNTRY_TITLE);
        when(userRepository.existsByUsername(username)).thenReturn(true);

        assertThrows(
                DataValidationException.class,
                () -> createUserValidator.validateUsername(requestDto));
        verify(userRepository, times(1)).existsByUsername(username);
    }

    @Test
    public void testValidateUsername_UsernameDoesNotExist_Success() {
        var username = "Username which is not existed";
        var requestDto = new CreateUserDto(username, TEST_EMAIL, TEST_PASSWORD, TEST_COUNTRY_TITLE);
        when(userRepository.existsByUsername(username)).thenReturn(false);

        assertDoesNotThrow(() -> createUserValidator.validateUsername(requestDto));
        verify(userRepository, times(1)).existsByUsername(username);
    }

    @Test
    public void testValidateUserEmail_UserEmailIsAlreadyExisted_Throws() {
        var userEmail = "Already existed user email";
        var requestDto = new CreateUserDto(TEST_USERNAME, userEmail, TEST_PASSWORD, TEST_COUNTRY_TITLE);
        when(userRepository.existsByEmail(userEmail)).thenReturn(true);

        assertThrows(
                DataValidationException.class,
                () -> createUserValidator.validateUserEmail(requestDto));
        verify(userRepository, times(1)).existsByEmail(userEmail);
    }

    @Test
    public void testValidateUserEmail_UserEmailDoesNotExist_Success() {
        var userEmail = "User email which is not existed";
        var requestDto = new CreateUserDto(TEST_USERNAME, userEmail, TEST_PASSWORD, TEST_COUNTRY_TITLE);
        when(userRepository.existsByEmail(userEmail)).thenReturn(false);

        assertDoesNotThrow(() -> createUserValidator.validateUserEmail(requestDto));
        verify(userRepository, times(1)).existsByEmail(userEmail);
    }

    @Test
    public void testValidateCountryTitle_CountryDoesNotExist_Throws() {
        var countryTitle = "Country which is not existed";
        var requestDto = new CreateUserDto(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD, countryTitle);
        when(countryRepository.findByTitle(countryTitle)).thenReturn(Optional.empty());

        assertThrows(
                DataValidationException.class,
                () -> createUserValidator.validateCountryTitle(requestDto));
        verify(countryRepository, times(1)).findByTitle(countryTitle);
    }

    @Test
    public void testValidateCountryTitle_CountryIsAlreadyExisted_ReturnsCountry() {
        var countryTitle = "Country email which is not existed";
        var requestDto = new CreateUserDto(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD, countryTitle);
        var expectedResult = Country.builder().title(countryTitle).build();
        when(countryRepository.findByTitle(countryTitle))
                .thenReturn(Optional.of(expectedResult));

        var result = createUserValidator.validateCountryTitle(requestDto);

        assertEquals(expectedResult.getTitle(), result.getTitle());
        verify(countryRepository, times(1)).findByTitle(countryTitle);
    }
}