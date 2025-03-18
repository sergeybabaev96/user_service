package school.faang.user_service.exception.data_validation_exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class UserExceptionHandler {

    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<?> catchException(DataValidationException e) {
        return ResponseEntity.status(e.getExceptionCode()).body(e.getMessage());
    }
}
