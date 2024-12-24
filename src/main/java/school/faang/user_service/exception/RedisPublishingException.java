package school.faang.user_service.exception;

public class RedisPublishingException extends RuntimeException {
    public RedisPublishingException(String message, Throwable cause) {
        super(message, cause);
    }
}
