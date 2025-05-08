package school.faang.user_service.exception;

import java.io.Serializable;

public class DataValidationException extends RuntimeException {
    public DataValidationException(String message) {
        super(message);
    }
}
