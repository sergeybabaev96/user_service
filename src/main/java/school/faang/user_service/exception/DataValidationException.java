package school.faang.user_service.exception;

public class DataValidationException extends IllegalArgumentException {

    public DataValidationException(String s) {
        super(s);
    }
}