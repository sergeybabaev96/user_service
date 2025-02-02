package school.faang.user_service.service.s3;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {
    void uploadFile(MultipartFile file, String folder);
    //void deleteFile(String key);
    //InputStream downloadFile(String key);
}
