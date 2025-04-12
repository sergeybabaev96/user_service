package school.faang.user_service.exception;

public class ConvertingDataException extends RuntimeException {

    public ConvertingDataException(String message, Object... args) {
        super(String.format(message, args));
    }
}
