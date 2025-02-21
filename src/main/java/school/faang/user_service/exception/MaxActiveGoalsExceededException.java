package school.faang.user_service.exception;

public class MaxActiveGoalsExceededException extends RuntimeException {
    public MaxActiveGoalsExceededException(String message) {
        super(message);
    }
}