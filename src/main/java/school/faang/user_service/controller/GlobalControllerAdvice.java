package school.faang.user_service.controller;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> argumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult()
                .getFieldErrors()
                 .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        logging(LoggingLevel.WARN, errors);
        return errors;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public Map<String, String> constraintViolationExceptionHandler(ConstraintViolationException e) {
        Map<String, String> errors = new HashMap<>();
        e.getConstraintViolations()
                .forEach(violation -> errors.
                        put(violation.getPropertyPath().toString(), violation.getMessage()));

        logging(LoggingLevel.WARN, errors);
        return errors;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Map.Entry<String, String> otherExceptionHandler(Exception e) {
        String cause = "Unknown";
        if (null != e.getCause()) {
            cause = e.getCause().toString();
        }

        Map.Entry<String, String> error = Map.entry(cause, e.getMessage());

        logging(LoggingLevel.ERROR, error);
        return error;
    }


    private void logging(LoggingLevel level, Map<String, String> errors) {
        errors.entrySet().forEach(entry -> logging(level, entry));
    }

    private void logging(LoggingLevel level, Map.Entry<String, String> error) {
        logging(level, String.format("%s: %s", error.getKey(), error.getValue()));
    }

    private void logging(LoggingLevel level, String errorMessage) {
        level.command.accept(errorMessage);
    }

    public enum LoggingLevel {
        INFO(log::info), DEBUG(log::debug), WARN(log::warn), ERROR(log::error);

        final Consumer<String> command;
        LoggingLevel(Consumer<String> command) {
            this.command = command;
        }
    }
}



