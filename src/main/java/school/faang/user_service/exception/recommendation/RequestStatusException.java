package school.faang.user_service.exception.recommendation;

public class RequestStatusException extends RuntimeException {
    public RequestStatusException(String message, Object... args) {
        super(String.format(message, args));
    }
}
