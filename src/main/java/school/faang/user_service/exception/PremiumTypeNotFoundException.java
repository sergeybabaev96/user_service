package school.faang.user_service.exception;

public class PremiumTypeNotFoundException extends RuntimeException {
    public PremiumTypeNotFoundException(String message) {
        super(message);
    }
}
