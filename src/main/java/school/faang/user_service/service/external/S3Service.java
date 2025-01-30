package school.faang.user_service.service.external;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.exception.S3Exception;

import java.io.ByteArrayInputStream;
import java.net.URL;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3Service {

    private final AmazonS3 amazonS3Client;

    @Value("${aws.s3.bucket-name:default-bucket}")
    private String bucketName;

    public void uploadToBucket(String fileName, byte[] data, String contentType) {
        log.info("Uploading file to S3 bucket: {}, fileName: {}", bucketName, fileName);

        ensureBucketExists(bucketName);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        metadata.setContentLength(data.length);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        try {
            PutObjectResult result = amazonS3Client.putObject(bucketName, fileName, inputStream, metadata);
            log.info("File uploaded successfully: {}, ETag: {}", fileName, result.getETag());
        } catch (SdkClientException e) {
            log.error("Failed to upload file to S3: {}", fileName, e);
            throw new S3Exception("Error uploading file to S3", e);
        }
    }

    public String getUnexpiredUrl(String fileId) {
        log.info("Generating presigned URL for file: {}, bucket: {}", fileId, bucketName);

        try {
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, fileId);
            URL url = amazonS3Client.generatePresignedUrl(request);
            log.info("Presigned URL generated successfully for file: {}", fileId);
            return url.toString();
        } catch (SdkClientException e) {
            log.error("Failed to generate presigned URL for file: {}, bucket: {}", fileId, bucketName, e);
            throw new S3Exception("Error generating presigned URL", e);
        }
    }

    private void ensureBucketExists(String bucketName) {
        try {
            if (!amazonS3Client.doesBucketExistV2(bucketName)) {
                log.info("Bucket does not exist, creating: {}", bucketName);
                amazonS3Client.createBucket(bucketName);
            }
        } catch (SdkClientException e) {
            log.error("Failed to ensure bucket exists: {}", bucketName, e);
            throw new S3Exception("Error ensuring bucket existence", e);
        }
    }
}