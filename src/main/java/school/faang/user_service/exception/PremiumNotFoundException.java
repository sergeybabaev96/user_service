package school.faang.user_service.exception;

public class PremiumNotFoundException extends RuntimeException {
    public PremiumNotFoundException(String message) {
        super(message);
    }
}