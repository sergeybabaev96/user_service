package school.faang.user_service.exception;

public class SkillLimitExceededException extends RuntimeException {
    public SkillLimitExceededException(String message) {
        super(message);
    }
}
