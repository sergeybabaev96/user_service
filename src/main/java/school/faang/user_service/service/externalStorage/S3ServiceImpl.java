package school.faang.user_service.service.externalStorage;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.externalStorage.ExternalResourceDto;
import school.faang.user_service.exception.ExternalServiceError;

import java.io.InputStream;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {
    private final AmazonS3 s3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Override
    public ExternalResourceDto uploadFile(
            InputStream inputStream,
            long dataSize,
            @Nullable String contentType,
            String filename,
            String folder) {
        var objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(dataSize);
        if (contentType == null) {
            contentType = MediaType.ALL_VALUE;
        }
        objectMetadata.setContentType(contentType);

        var key = getResourceKey(folder, filename);

        var s3Request = new PutObjectRequest(bucketName, key, inputStream, objectMetadata);

        try {
            s3Client.putObject(s3Request);
        } catch (Exception ex) {
            var errorMessage = "Cannot send %s file %s to AWS folder: %s/%s".formatted(
                    contentType,
                    filename,
                    bucketName,
                    folder);
            log.error(errorMessage, ex);

            throw new ExternalServiceError(errorMessage, ex);
        }

        var creationDateTime = LocalDateTime.now();

        return new ExternalResourceDto(
                key,
                BigInteger.valueOf(dataSize),
                creationDateTime,
                creationDateTime,
                contentType,
                filename);
    }

    @Override
    public void deleteFile(String key) {
        var s3Request = new DeleteObjectRequest(bucketName, key);

        try {
            s3Client.deleteObject(s3Request);
        } catch (Exception ex) {
            var errorMessage = "Cannot delete object %s AWS in folder: %s".formatted(key, bucketName);
            log.error(errorMessage, ex);

            throw new ExternalServiceError(errorMessage, ex);
        }
    }

    private String getResourceKey(String folder, String fileName) {
        return "%s:%s/%s".formatted(UUID.randomUUID().toString(), folder, fileName);
    }
}
