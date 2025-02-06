package school.faang.user_service.exception;

public class GoalCannotBeCompletedException extends RuntimeException {
    public GoalCannotBeCompletedException(String message) {
        super(message);
    }
}
