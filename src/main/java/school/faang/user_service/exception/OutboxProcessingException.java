package school.faang.user_service.exception;

public class OutboxProcessingException extends RuntimeException {
    public OutboxProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
