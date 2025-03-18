package school.faang.user_service.exception.goal;

public class GoalAlreadyCompletedException extends RuntimeException {
    public GoalAlreadyCompletedException(String message) {
        super(message);
    }
}
