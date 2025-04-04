package school.faang.user_service.exception;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;
import school.faang.user_service.dto.error.ErrorResponse;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({
            UserNotFoundException.class,
            AmazonS3Exception.class
    })
    @ResponseStatus(NOT_FOUND)
    public ErrorResponse handleExceptionsWithStatusNotFound(Exception ex) {
        return getErrorResponse(ex, NOT_FOUND);
    }

    @ExceptionHandler({
            FileTooLargeException.class,
            InvalidImageFormatException.class,
            MultipartException.class,
    })
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleExceptionsWithStatusBadRequest(Exception ex) {
        return getErrorResponse(ex, BAD_REQUEST);
    }

    private ErrorResponse getErrorResponse(Exception ex, HttpStatus status) {
        log.error("{}", ex.toString());
        return ErrorResponse.builder()
                .message(ex.getMessage())
                .statusCode(status.value())
                .statusName(status.name())
                .build();
    }
}
