package school.faang.user_service.service.user;

import org.springframework.core.io.ByteArrayResource;

import java.io.InputStream;

public interface MinioService {

    String uploadToMinio(ByteArrayResource file, String bucketName);

    InputStream downloadFile(String bucketName, String key);

}