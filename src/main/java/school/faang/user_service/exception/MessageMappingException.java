package school.faang.user_service.exception;

public class MessageMappingException extends RuntimeException {
    public MessageMappingException(String message, Exception error) {
        super(message, error);
    }
}
