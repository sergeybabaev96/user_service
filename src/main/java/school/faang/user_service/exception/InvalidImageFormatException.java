package school.faang.user_service.exception;

public class InvalidImageFormatException extends RuntimeException {

    public InvalidImageFormatException(String message, Object... args) {
        super(String.format(message, args));
    }
}
