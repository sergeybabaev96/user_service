package school.faang.user_service.exception;

public class PremiumInvalidDataException extends RuntimeException {
    public PremiumInvalidDataException(String message) {
        super(message);
    }
}