package school.faang.user_service.validation;

import lombok.extern.slf4j.Slf4j;
import school.faang.user_service.exceptions.DataValidationException;

import java.time.LocalDate;

@Slf4j
public class UserServiceValidation {

    private static final String LESS_CURRENT_YEAR = "The start date of education must be less than the current date.\"";
    private static final String DATA_LOG_ERROR = "Data {} is not correct\"";

    public static void yearFromEducationValidCurrentDate(Integer yearFrom) {
        if (yearFrom > LocalDate.now().getYear()) {
            log.error(DATA_LOG_ERROR, yearFrom);
            throw new DataValidationException(LESS_CURRENT_YEAR);
        }
    }

}
