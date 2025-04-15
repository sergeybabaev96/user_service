package school.faang.user_service.exception;

public class GoalAlreadyCompletedException extends RuntimeException {
    public GoalAlreadyCompletedException(String message) {
        super(message);
    }
}
