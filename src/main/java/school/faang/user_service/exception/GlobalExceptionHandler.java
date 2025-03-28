package school.faang.user_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    //400
    @ExceptionHandler(CsvParseException .class)
    public ResponseEntity<Object> handleCsvParseException(CsvParseException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().withNano(0));
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "CsvParseException");
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    //500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneric(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().withNano(0));
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("message", cleanMessage(ex));

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<String> handleDataValidationException(DataValidationException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().withNano(0));
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Validation error");

        body.put("message", message);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CsvParseException .class)
    public ResponseEntity<Object> handleCsvParseException(CsvParseException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, "CsvParseException");
    }

    @ExceptionHandler(FileSizeExceedLimitException.class)
    public ResponseEntity<Object> handleFileSizeExceedLimitException(FileSizeExceedLimitException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, "FileSizeExceedLimitException");
    }

    @ExceptionHandler(FileModificationException.class)
    public ResponseEntity<Object> handleFileModificationException(FileModificationException ex) {
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, "FileModificationException");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneric(Exception ex) {
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
    }

    private ResponseEntity<Object> buildErrorResponse(Exception ex, HttpStatus status, String error) {
        return buildErrorResponse(cleanMessage(ex), status, error);
    }

    private ResponseEntity<Object> buildErrorResponse(String message, HttpStatus status, String error) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().withNano(0));
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        return new ResponseEntity<>(body, status);
    }

    private String cleanMessage(Exception ex) {
        String message = ex.getMessage();
        if (message != null) {
            int index = message.indexOf("(");
            if (index != -1) {
                message = message.substring(0, index).trim();
            }
        }
        return message;
    }
}
