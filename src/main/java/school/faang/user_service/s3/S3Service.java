package school.faang.user_service.s3;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.FileException;

import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 s3Client;

    @Value("${services.s3.bucketName}")
    private String bucket;

    public String uploadFile(MultipartFile file, String folder) {
        long fileSize = file.getSize();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileSize);
        objectMetadata.setContentType(file.getContentType());
        String key = String.format("%s/%s", folder, file.getOriginalFilename());

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucket, key, file.getInputStream(), objectMetadata);
            s3Client.putObject(putObjectRequest);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new FileException(exception.getMessage());
        }
        return key;
    }

    public InputStream downloadFile(String fileId) {
        try {
            S3Object s3Object = s3Client.getObject(bucket, fileId);
            return s3Object.getObjectContent();
        } catch (Exception e) {
            throw new FileException(e.getMessage());
        }
    }

    public void deleteFile(String fileId) {
        try {
            s3Client.deleteObject(bucket, fileId);
        } catch (Exception e) {
            throw new FileException(e.getMessage());
        }
    }
}

