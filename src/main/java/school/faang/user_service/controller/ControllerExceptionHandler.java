package school.faang.user_service.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import school.faang.user_service.common.Currency;
import school.faang.user_service.dto.ErrorResponse;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.exception.PaymentException;
import school.faang.user_service.exception.PremiumBadRequestException;
import school.faang.user_service.exception.PremiumInvalidDataException;
import school.faang.user_service.exception.PremiumNotFoundException;
import school.faang.user_service.exception.ServiceNotAvailableException;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException e) {
        return new ErrorResponse(e.getMessage() != null ? e.getMessage() : "Entity not found");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException e) {
        return new ErrorResponse(e.getMessage() != null ? e.getMessage() : "Invalid argument");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> {
                    String field = ((FieldError) error).getField();
                    String errorMessage = Objects.requireNonNullElse(error.getDefaultMessage(), "Invalid value");
                    return field + ": " + errorMessage;
                })
                .collect(Collectors.joining("; "));

        return new ErrorResponse(message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        String message = e.getMessage()
                .contains("Currency") ? "We only accept " + Arrays.toString(Currency.values())
                : e.getMessage();

        return new ErrorResponse(message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(ConstraintViolationException e) {
        String errorMessage = e.getConstraintViolations()
                .stream()
                .map(violation -> String.format("Field '%s': %s",
                        violation.getPropertyPath(), violation.getMessage()))
                .collect(Collectors.joining("; ")); // Собираем все сообщения об ошибках в одну строку

        return new ErrorResponse(errorMessage);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRuntimeException(RuntimeException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(PremiumInvalidDataException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlePremiumInvalidDataException(PremiumInvalidDataException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(PremiumNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlePremiumNotFoundException(PremiumNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(PremiumBadRequestException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlePremiumBadRequestException(PremiumBadRequestException e) {
        return new ErrorResponse(e.getMessage());
    }
    @ExceptionHandler(ServiceNotAvailableException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleServiceNotAvailableException(ServiceNotAvailableException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(PaymentException.class)
    public ErrorResponse handlePaymentException(PaymentException e) {
        log.error("PaymentException: {}", e.getMessage());

        return new ErrorResponse(e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(BusinessException.class)
    public ErrorResponse handleBusinessException(BusinessException e) {
        log.error("BusinessException: {}", e.getMessage());

        return new ErrorResponse(e.getMessage());
    }
}
