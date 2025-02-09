package school.faang.user_service.service.external;

import com.amazonaws.SdkClientException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.exception.CustomS3Exception;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import java.time.Duration;

@RequiredArgsConstructor
@Service
public class S3Service {
    private final S3Client amazonS3Client;
    private final S3Presigner s3Presigner;

    public String uploadToBucket(String bucketName, String fileName, byte[] data, String contentType) {
        ensureBucketExists(bucketName);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(contentType)
                .contentLength((long) data.length)
                .build();

        executeWithS3Exception(() -> amazonS3Client.putObject(putObjectRequest, RequestBody.fromBytes(data)));
        return fileName;
    }

    public String getUnexpiredUrl(String bucketName, String fileId) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileId)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(60))
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
        return presignedRequest.url().toString();
    }

    public byte[] getUserAvatar(String bucketName, String key) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        ResponseBytes<GetObjectResponse> response = amazonS3Client.getObjectAsBytes(request);
        return response.asByteArray();
    }

    private void ensureBucketExists(String bucketName) {
        executeWithS3Exception(() -> {
            try {
                amazonS3Client.headBucket(HeadBucketRequest.builder()
                        .bucket(bucketName)
                        .build());
            } catch (NoSuchBucketException exception) {
                amazonS3Client.createBucket(CreateBucketRequest.builder()
                        .bucket(bucketName)
                        .build());
            }
        });
    }

    private void executeWithS3Exception(Runnable runnable) {
        try {
            runnable.run();
        } catch (SdkClientException e) {
            throw new CustomS3Exception("Error working with S3");
        }
    }
}
