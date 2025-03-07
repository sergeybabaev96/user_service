package school.faang.user_service.validator;

import school.faang.user_service.exception.DataValidationException;

public class IdValidator {
    public static void validateId(long id) {
        if (id <= 0) {
            throw new DataValidationException("Id is less than or equal to zero");
        }
    }
}
