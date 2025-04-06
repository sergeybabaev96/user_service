package school.faang.user_service.exception;

public class UsernameNotUniqueException extends RuntimeException {
    public UsernameNotUniqueException(String message) {
        super(message);
    }
}