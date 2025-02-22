package school.faang.user_service.exception;

public class NonExistentSkillException extends RuntimeException {
    public NonExistentSkillException(String message) {
        super(message);
    }
}