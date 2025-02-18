package school.faang.user_service.exception;

public class InactiveGoalUpdateException extends RuntimeException {
    public InactiveGoalUpdateException(String message) {
        super(message);
    }
}
