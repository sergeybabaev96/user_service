package school.faang.user_service.service.user;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.exception.UploadResourceException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;


@Slf4j
@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

    private final AmazonS3 s3Client;

    public String uploadToS3(byte[] file, String bucketName, String key, String contentType) {
        checkBucketExists(bucketName);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(contentType);
        objectMetadata.setContentLength(file.length);

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(file)) {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, inputStream, objectMetadata);
            s3Client.putObject(putObjectRequest);
        } catch (Exception e) {
            log.error("Failed to save avatar to S3: ", e);
            throw new UploadResourceException("Failed to save avatar to S3");
        }
        return key;
    }

    @Override
    public String uploadToS3(File file, String bucketName, String key) {
        checkBucketExists(bucketName);

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file);
        s3Client.putObject(putObjectRequest);
        return key;
    }

    @Override
    public InputStream downloadFile(String bucketName, String key) {
        GetObjectRequest getObjectArgs = new GetObjectRequest(bucketName, key);
        S3Object object = s3Client.getObject(getObjectArgs);
        if (object == null) {
            throw new EntityNotFoundException(String.format("Resource not found by key = %s", key));
        }
        return object.getObjectContent();
    }

    @Override
    public void deleteFile(String bucketName, String key) {
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucketName, key);
        s3Client.deleteObject(deleteObjectRequest);
    }

    @SneakyThrows
    private void checkBucketExists(String bucketName) {
        boolean isBucketExists = s3Client.doesBucketExistV2(bucketName);
        if (!isBucketExists) {
            s3Client.createBucket(bucketName);
        }
    }
}
