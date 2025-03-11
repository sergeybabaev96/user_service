package school.faang.user_service.validator.career;

import lombok.extern.slf4j.Slf4j;
import school.faang.user_service.dto.CareerDto;
import school.faang.user_service.exception.DataValidationException;

import java.time.LocalDate;

@Slf4j
public class CareerValidator {
    private static final String ERROR_FROM_DATE_EMPTY = "Data from can't be empty";
    private static final String ERROR_FROM_DATE_IN_THE_FUTURE = "Date {} is not correct";
    private static final String ERROR_COMPANY_EMPTY = "Fields company can't be empty";
    private static final String ERROR_POSITION_EMPTY = "Fields position can't be empty";

    public static void validate(CareerDto careerDto) {
        if(careerDto.getFrom() == null) {
            log.error(ERROR_FROM_DATE_EMPTY);
            throw new DataValidationException(ERROR_FROM_DATE_EMPTY);
        }
        if(careerDto.getFrom().isAfter(LocalDate.now())) {
            log.error(ERROR_FROM_DATE_IN_THE_FUTURE, careerDto.getFrom());
            throw new DataValidationException(String.format(ERROR_FROM_DATE_IN_THE_FUTURE));
        }
        if(careerDto.getCompany() == null || careerDto.getCompany().isBlank()) {
            log.error(ERROR_COMPANY_EMPTY);
            throw new DataValidationException(ERROR_COMPANY_EMPTY);
        }
        if(careerDto.getPosition() == null || careerDto.getPosition().isBlank()) {
            log.error(ERROR_POSITION_EMPTY);
            throw new DataValidationException(ERROR_POSITION_EMPTY);
        }
    }
}
