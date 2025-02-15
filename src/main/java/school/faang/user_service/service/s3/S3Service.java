package school.faang.user_service.service.s3;

import java.io.File;
import java.io.InputStream;

public interface S3Service {

    String uploadToS3(byte[] file, String bucketName, String key, String contentType);

    String uploadToS3(File file, String bucketName, String key);

    InputStream downloadFile(String bucketName, String key);

    void deleteFile(String bucketName, String key);
}