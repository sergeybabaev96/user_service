package school.faang.user_service.exception;

public class MaxActiveGoalsLimitExceededException extends RuntimeException {

    public MaxActiveGoalsLimitExceededException(String message) {
        super(message);
    }
}