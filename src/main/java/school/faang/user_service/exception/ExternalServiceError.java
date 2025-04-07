package school.faang.user_service.exception;

public class ExternalServiceError extends RuntimeException {
    public ExternalServiceError(String message) {
        super(message);
    }

    public ExternalServiceError(String message, Throwable cause) {
        super(message, cause);
    }
}
