package school.faang.user_service.controller.handler;

import io.minio.errors.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import school.faang.user_service.dto.error.ErrorField;
import school.faang.user_service.dto.error.ErrorResponse;
import school.faang.user_service.exception.*;

import java.io.IOException;
import java.rmi.ServerException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationExceptions(MethodArgumentNotValidException exception, WebRequest webRequest) {
        String path = webRequest.getDescription(false).replace("uri=", "");

        List<ErrorField> details = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> new ErrorField(error.getField(), error.getDefaultMessage()))
                .toList();

        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .path(path)
                .message("Validation failed")
                .details(details)
                .build();
    }

    @ExceptionHandler(value = {
            UserWasNotFoundException.class,
            ResourceNotFoundException.class,
            SkillNotFoundException.class,
            UserProfileWasNotFound.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorResponse userWasNotFound(Exception exception, WebRequest webRequest) {
        return buildErrorMessage(exception, webRequest);
    }

    @ExceptionHandler(value = {
            DataValidationException.class,
            UserGoalLimitExceededException.class,
            FileSizeIncorrectException.class,
            FileTypeIncorrectException.class,
            MaxUploadSizeExceededException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataValidation(Exception exception, WebRequest webRequest) {
        return buildErrorMessage(exception, webRequest);
    }

    @ExceptionHandler(value = {MinioSaveException.class, PaymentServiceException.class, PaymentPayException.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleMinioSave(MinioSaveException exception, WebRequest webRequest) {
        return buildErrorMessage(exception, webRequest);
    }


    @ExceptionHandler(value = {RecommendationRequestCreatedException.class, 
                               RequestAlreadyProcessedException.class, 
                               UserAlreadyExistsException.class, 
                               PremiumAlreadyExistsException.class})
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ErrorResponse handleRecommendationRequestCreated(Exception exception, WebRequest webRequest) {
        return buildErrorMessage(exception, webRequest);
    }

    @ExceptionHandler({
            IOException.class,
            ServerException.class,
            InsufficientDataException.class,
            ErrorResponseException.class,
            NoSuchAlgorithmException.class,
            InvalidKeyException.class,
            InvalidResponseException.class,
            XmlParserException.class,
            InternalException.class
    })
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleMinioExceptions(Exception exception, WebRequest webRequest) {
        return buildErrorMessage(exception, webRequest);
    }

    private ErrorResponse buildErrorMessage(Exception exception, WebRequest webRequest) {
        String path = webRequest.getDescription(false).replace("uri=", "");
        return ErrorResponse.builder()
                .message(exception.getMessage())
                .path(path)
                .build();
    }
}
