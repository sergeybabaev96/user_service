package school.faang.user_service.exception;

public class AuthorizationError extends RuntimeException {
    public AuthorizationError(String message) {
        super(message);
    }
}
