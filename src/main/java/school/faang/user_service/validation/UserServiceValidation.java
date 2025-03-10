package school.faang.user_service.validation;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import school.faang.user_service.exceptions.DataValidationException;

import java.time.LocalDate;

@Slf4j
@UtilityClass
public class UserServiceValidation {

    private final String LESS_CURRENT_YEAR = "The start date of education must be less than the current date.\"";
    private final String DATA_LOG_ERROR = "Data {} is not correct\"";

    public void validYearLessCurrentYear(Integer yearFrom) {
        if (yearFrom > LocalDate.now().getYear()) {
            log.error(DATA_LOG_ERROR, yearFrom);
            throw new DataValidationException(LESS_CURRENT_YEAR);
        }
    }

}
