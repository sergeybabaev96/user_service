package school.faang.user_service.validation;

public class DataValidationException extends RuntimeException{
    public DataValidationException(String message) {
        super(message);
    }
}