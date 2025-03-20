package school.faang.user_service.exception.skill;

public class SkillLimitExceededException extends RuntimeException {
    public SkillLimitExceededException(String message) {
        super(message);
    }
}
