package school.faang.user_service.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.constants.goal.ImageConstants;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3StorageService {

    private final AmazonS3 amazonS3;

    @Value("${s3.otherBuckets}")
    private String bucketNameForImage;

    public String uploadFile(MultipartFile file, String fileName) {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            amazonS3.putObject(new PutObjectRequest(bucketNameForImage, fileName, file.getInputStream(), metadata));
        } catch (IOException ex) {
            log.error("Input output error. {}", ex.toString());
            throw new RuntimeException(ex.getMessage());
        }
        return fileName;
    }

    public void deleteFile(String fileName) {
        amazonS3.deleteObject(bucketNameForImage, fileName);
    }

    public InputStream getFile(String fileName) {
        S3Object objectFile = amazonS3.getObject(bucketNameForImage, fileName);
        return objectFile.getObjectContent();
    }

    public String getContentType(String fileName) {
        ObjectMetadata metadata = amazonS3.getObjectMetadata(bucketNameForImage, fileName);
        String contentType = metadata.getContentType();

        if (contentType != null && contentType.matches(ImageConstants.IMAGE_FORMAT_REGEX)) {
            return contentType;
        }
        return ImageConstants.DEFAULT_FORMAT;
    }
}
