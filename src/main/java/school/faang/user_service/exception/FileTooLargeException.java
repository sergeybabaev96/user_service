package school.faang.user_service.exception;

public class FileTooLargeException extends RuntimeException {

  public FileTooLargeException(String message, Object... args) {
        super(String.format(message, args));
    }
}
