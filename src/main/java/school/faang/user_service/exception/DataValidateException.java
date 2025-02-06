package school.faang.user_service.exception;

public class DataValidateException extends RuntimeException{
    public DataValidateException(String message) {
        super(message);
    }
}