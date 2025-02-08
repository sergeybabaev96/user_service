package school.faang.user_service.exception;

public class UserProfileWasNotFound extends RuntimeException {
    public UserProfileWasNotFound(String message) {
        super(message);
    }
}
