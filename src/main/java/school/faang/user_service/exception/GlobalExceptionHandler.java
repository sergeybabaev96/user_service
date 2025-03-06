package school.faang.user_service.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<String> handleValidationException(DataValidationException e) {
        log.warn("Validation error: {}", e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
