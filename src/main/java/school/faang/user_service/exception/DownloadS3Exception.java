package school.faang.user_service.exception;

public class DownloadS3Exception extends RuntimeException {

    public DownloadS3Exception(String message) {
        super(message);
    }
}