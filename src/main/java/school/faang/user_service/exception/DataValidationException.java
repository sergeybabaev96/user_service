package school.faang.user_service.exception;

public class DataValidationException extends RuntimeException {
    public DataValidationException(String message, Object... args) {
        super(ExceptionMessageFormatter.format(message, args));
    }
}
