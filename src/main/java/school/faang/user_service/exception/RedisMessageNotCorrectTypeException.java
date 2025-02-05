package school.faang.user_service.exception;

public class RedisMessageNotCorrectTypeException extends RuntimeException {
    public RedisMessageNotCorrectTypeException(String message) {
        super(message);
    }
}
