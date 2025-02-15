package school.faang.user_service.service.user;

import java.io.InputStream;

public interface S3Service {

    String uploadToS3(byte[] file, String bucketName, String key, String contentType);

    InputStream downloadFile(String bucketName, String key);

}