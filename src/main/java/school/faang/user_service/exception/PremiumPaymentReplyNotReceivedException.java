package school.faang.user_service.exception;

public class PremiumPaymentReplyNotReceivedException extends RuntimeException {
    public PremiumPaymentReplyNotReceivedException(String message) {
        super(message);
    }
}
