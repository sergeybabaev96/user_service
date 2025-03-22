package school.faang.user_service.exception;

public class ExternalResourceNotFoundException extends RuntimeException {
    public ExternalResourceNotFoundException(String message) {
        super(message);
    }

    public ExternalResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
