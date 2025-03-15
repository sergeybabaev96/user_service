package school.faang.user_service.exception;

public class MentorshipNotFoundException extends RuntimeException {
    public MentorshipNotFoundException(String message) {
        super(message);
    }
}
