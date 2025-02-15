package school.faang.user_service.exception;

public class UploadS3Exception extends RuntimeException {

    public UploadS3Exception(String message) {
        super(message);
    }
}