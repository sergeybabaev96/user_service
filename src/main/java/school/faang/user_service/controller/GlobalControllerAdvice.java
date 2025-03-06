package school.faang.user_service.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> argumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        Map<String, String> errors = getErrorsMapWithExceptionTitle(e);

        e.getBindingResult()
                .getFieldErrors()
                 .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        logging(LoggingLevel.WARN, errors);
        return errors;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public Map<String, String> constraintViolationExceptionHandler(jakarta.validation.ConstraintViolationException e) {
        Map<String, String> errors = getErrorsMapWithExceptionTitle(e);

        e.getConstraintViolations()
                .forEach(violation -> {
                    errors.put(violation.getRootBeanClass().toString(), violation.getRootBean().toString());
                    errors.put(violation.getPropertyPath().toString(), violation.getMessage());
                });

        logging(LoggingLevel.WARN, errors);
        return errors;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public Map<String, String> constraintViolationExceptionHandler(org.springframework.dao.DataIntegrityViolationException e) {
        Map<String, String> errors = getErrorsMapWithExceptionTitle(e);

        Throwable rootCause = e.getRootCause();
        if (null == rootCause) {
            return errors;
        }
        errors.put("Root cause", rootCause.toString());

        if (rootCause instanceof org.hibernate.exception.ConstraintViolationException hibernateEx) {
            String sqlMessage = hibernateEx.getSQLException().getMessage();
            String constraintName = hibernateEx.getConstraintName();
            errors.put("SQL error", sqlMessage);
            errors.put("Violation of validation", constraintName);
        } else if (rootCause instanceof java.sql.SQLException sqlEx) {
            errors.put("SQL error", sqlEx.getMessage());
        } else {
            errors.put("Unknown error", e.getMessage());
        }

        logging(LoggingLevel.WARN, errors);
        return errors;
    }

    @ExceptionHandler(org.hibernate.JDBCException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleJDBCException(org.hibernate.JDBCException e) {
        Map<String, String> errors = getErrorsMapWithExceptionTitle(e);

        java.sql.SQLException sqlException = e.getSQLException();
        String sqlMessage = sqlException.getMessage();
        int errorCode = sqlException.getErrorCode();
        String sqlState = sqlException.getSQLState();

        String title = "SQLException ";
        errors.put(title + "error", "Database error occurred");
        errors.put(title + "message", sqlMessage);
        errors.put(title + "code", String.valueOf(errorCode));
        errors.put(title + "state", sqlState);

        return errors;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Map<String, String> otherExceptionHandler(Exception e) {
        Map<String, String> errors = getErrorsMapWithExceptionTitle(e);

        String cause = "Unknown";
        if (null != e.getCause()) {
            cause = e.getCause().toString();
        }

        errors.put("Cause", cause);

        logging(LoggingLevel.ERROR, errors);
        return errors;
    }


    private Map<String, String> getErrorsMapWithExceptionTitle(Exception e) {
        Map<String, String> errors = new LinkedHashMap<>();

        errors.put("Exception", e.getClass().toString());
        errors.put("Exception message", e.getMessage());

        return errors;
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