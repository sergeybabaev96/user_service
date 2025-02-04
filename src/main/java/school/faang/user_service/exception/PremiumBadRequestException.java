package school.faang.user_service.exception;

public class PremiumBadRequestException extends RuntimeException {
    public PremiumBadRequestException(String message) {
        super(message);
    }
}