package school.faang.user_service.handler;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import school.faang.user_service.exception.DataValidationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<String> handleDataValidationException(DataValidationException ex) {
        return BadRequest(ex);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException ex) {
        return BadRequest(ex);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return InternalServerError(ex);
    }

    private ResponseEntity<String> InternalServerError(Exception ex) {
        return getResponse(ex, 500);
    }

    private ResponseEntity<String> BadRequest(Exception ex) {
        return getResponse(ex, 400);
    }

    private ResponseEntity<String> getResponse(Exception ex, int code) {
        return ResponseEntity
                .status(HttpStatus.valueOf(code))
                .body(ex.getMessage());
    }
}
