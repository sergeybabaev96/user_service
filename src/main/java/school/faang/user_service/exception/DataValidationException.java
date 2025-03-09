package school.faang.user_service.exception;

public class DataValidationException extends IllegalArgumentException {
    public DataValidationException() {
        super();
    }
    
    public DataValidationException(String message) {
        super(message);
    }
}