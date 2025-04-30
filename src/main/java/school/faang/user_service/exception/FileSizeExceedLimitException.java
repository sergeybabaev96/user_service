package school.faang.user_service.exception;

public class FileSizeExceedLimitException extends RuntimeException {
    public FileSizeExceedLimitException(String message) {
        super(message);
    }
}
