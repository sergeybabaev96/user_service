package school.faang.user_service.validator;

import school.faang.user_service.exception.DataValidationException;

public class IdValidator {
    public static void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new DataValidationException("Id is less than or equal to zero");
        }
    }
}
