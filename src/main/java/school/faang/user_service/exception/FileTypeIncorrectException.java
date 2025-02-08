package school.faang.user_service.exception;

public class FileTypeIncorrectException extends RuntimeException {
    public FileTypeIncorrectException(String message) {
        super(message);
    }
}
