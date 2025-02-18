package school.faang.user_service.exception;

public class UnsupportedGoalStatusException extends RuntimeException {
    public UnsupportedGoalStatusException(String message) {
        super(message);
    }
}
