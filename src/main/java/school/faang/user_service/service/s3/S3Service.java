package school.faang.user_service.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 s3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    private static final long PRESIGNED_URL_EXPIRATION = 432_000_000L;

    public void uploadFile(String fileId, ByteArrayInputStream inputStream, long contentLength, String contentType) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(contentLength);
        metadata.setContentType(contentType);

        s3Client.putObject(new PutObjectRequest(bucketName, fileId, inputStream, metadata));
        log.info("Uploaded file to S3 with key={}", fileId);
    }

    public void deleteFile(String fileId) {
        s3Client.deleteObject(bucketName, fileId);
        log.info("Deleted file from S3 with key={}", fileId);
    }

    public String generatePresignedUrl(String fileId) {
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, fileId)
                .withExpiration(new Date(System.currentTimeMillis() + PRESIGNED_URL_EXPIRATION));
        URL url = s3Client.generatePresignedUrl(request);
        return url.toString();
    }
}