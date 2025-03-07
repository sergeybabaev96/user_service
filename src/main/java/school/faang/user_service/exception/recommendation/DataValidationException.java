package school.faang.user_service.exception.recommendation;

public class DataValidationException extends RuntimeException {
    public DataValidationException(String message, Object... args) {
        super(String.format(message, args));
    }
}
