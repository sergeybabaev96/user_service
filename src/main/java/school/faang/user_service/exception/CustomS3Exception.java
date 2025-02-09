package school.faang.user_service.exception;

public class CustomS3Exception extends RuntimeException {
    public CustomS3Exception(String message) {
        super(message);
    }
}
