package school.faang.user_service.validator.education;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class UserServiceValidationTest {

    @Test
    public void testValidYearLessCurrentYear_ValidYear() {
        int validYear = LocalDate.now().minusYears(1).getYear();
        assertDoesNotThrow(() -> UserServiceValidation.validYearLessCurrentYear(validYear));
    }

    @Test
    @DisplayName("Test validYearLessCurrentYear with current year")
    public void testValidYearLessCurrentYear_CurrentYear() {
        int currentYear = LocalDate.now().getYear();
        assertDoesNotThrow(() -> UserServiceValidation.validYearLessCurrentYear(currentYear));
    }

    @Test
    @DisplayName("Test validYearLessCurrentYear with future year")
    public void testValidYearLessCurrentYear_FutureYear() {
        Integer futureYear = LocalDate.now().plusYears(1).getYear();
        assertThrows(DataValidationException.class,
                () -> UserServiceValidation.validYearLessCurrentYear(futureYear));

    }
}