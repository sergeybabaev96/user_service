package school.faang.user_service.exception;

public class FileSizeIncorrectException extends RuntimeException {
    public FileSizeIncorrectException(String message) {
        super(message);
    }
}
