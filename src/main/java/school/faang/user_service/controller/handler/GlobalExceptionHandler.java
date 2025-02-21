package school.faang.user_service.controller.handler;

import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import school.faang.user_service.dto.error.ErrorModel;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.KafkaProduceException;
import school.faang.user_service.exception.MaxActiveGoalsLimitExceededException;
import school.faang.user_service.exception.ResourceNotFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Value("${service.name}")
    private String serviceName;

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorModel handleDataValidationException(DataValidationException ex) {
        log.error("Data validation exception", ex);
        return createError(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorModel handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error("Resource not found exception", ex);
        return createError(ex.getMessage(), HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorModel handleEntityNotFoundException(EntityNotFoundException ex) {
        log.error("Entity not found exception", ex);
        return createError(ex.getMessage(), HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorModel handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Illegal argument exception", ex);
        return createError(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(FeignException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public ErrorModel handleFeignException(FeignException ex) {
        log.error("Feign exception", ex);
        return createError(ex.getMessage(), HttpStatus.BAD_GATEWAY.value());
    }

    @ExceptionHandler(KafkaProduceException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public ErrorModel handleKafkaProduceException(KafkaProduceException ex) {
        log.error("Kafka produce exception", ex);
        return createError(ex.getMessage(), HttpStatus.BAD_GATEWAY.value());
    }

    @ExceptionHandler(MaxActiveGoalsLimitExceededException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorModel handleMaxActiveGoalsLimitExceedException(MaxActiveGoalsLimitExceededException ex) {
        log.error("Max active goals limit exceeded exception", ex);
        return createError(ex.getMessage(), HttpStatus.CONFLICT.value());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorModel handleGenericException(Exception ex) {
        log.error("Internal server error exception", ex);
        return createError(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private ErrorModel createError(String message, int statusCode) {
        return new ErrorModel(message, statusCode, serviceName);
    }
}
