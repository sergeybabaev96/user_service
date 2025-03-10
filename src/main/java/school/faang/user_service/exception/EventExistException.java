package school.faang.user_service.exception;

public class EventExistException extends RuntimeException {

    public EventExistException(String message) {
        super(message);
    }
}
