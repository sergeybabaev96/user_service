package school.faang.user_service.exception;

public class PremiumNotActiveException extends RuntimeException {
    public PremiumNotActiveException(String message) {
        super(message);
    }
}
