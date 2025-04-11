package school.faang.user_service.exception;

public class NotEnoughOffersException extends RuntimeException {
    public NotEnoughOffersException(String message) {
        super(message);
    }
}