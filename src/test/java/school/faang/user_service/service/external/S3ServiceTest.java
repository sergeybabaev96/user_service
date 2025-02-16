package school.faang.user_service.service.external;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.CustomS3Exception;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.URL;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {
    @Mock
    private S3Client amazonS3Client;

    @InjectMocks
    S3Service s3Service;
    String bucketName;
    String fileName;
    byte[] data;
    String contentType;


    @BeforeEach
    void setUp() {
        bucketName = "user-avatars";
        fileName = "file";
        data = fileName.getBytes();
        contentType = "image/svg+xml";
    }

    @Test
    void testUploadToBucketThrowsS3Exception() {
        when(amazonS3Client.headBucket(any(HeadBucketRequest.class)))
                .thenThrow(NoSuchBucketException.builder()
                        .message("Bucket not found")
                        .build());
        when(amazonS3Client.createBucket(any(CreateBucketRequest.class))).thenReturn(null);
        when(amazonS3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(SdkClientException.builder().message("Ex").build());

        CustomS3Exception exception = assertThrows(CustomS3Exception.class,
                () -> s3Service.uploadToBucket(bucketName, fileName, data, contentType));

        assertEquals("Error working with S3", exception.getMessage());
    }

    @Test
    void testUploadToBucketCorrect() {
        when(amazonS3Client.headBucket(any(HeadBucketRequest.class)))
                .thenThrow(NoSuchBucketException.builder()
                        .message("Bucket not found")
                        .build());
        when(amazonS3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenReturn(null);
        String actualFileName = s3Service.uploadToBucket(bucketName, fileName, data, contentType);

        assertEquals(fileName, actualFileName);

        verify(amazonS3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void testGetUnexpiredUrl() throws Exception {
        String expectedUrl = "http://localhost/test-bucket/test-file.txt?X";
        URL mockUrl = new URL(expectedUrl);
        S3Presigner s3Presigner = mock(S3Presigner.class);
        S3Client client = mock(S3Client.class);
        S3Service s3Service = new S3Service(client, s3Presigner);
        PresignedGetObjectRequest mockPresignedRequest = mock(PresignedGetObjectRequest.class);

        when(mockPresignedRequest.url()).thenReturn(mockUrl);
        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class))).thenReturn(mockPresignedRequest);

        String actualUrl = s3Service.getUnexpiredUrl(bucketName, fileName);

        assertEquals(expectedUrl, actualUrl);
        ArgumentCaptor<GetObjectPresignRequest> captor = ArgumentCaptor.forClass(GetObjectPresignRequest.class);
        verify(s3Presigner).presignGetObject(captor.capture());

        GetObjectPresignRequest capturedRequest = captor.getValue();
        assertEquals(Duration.ofMinutes(60), capturedRequest.signatureDuration());

        verify(s3Presigner, times(1)).presignGetObject(any(GetObjectPresignRequest.class));
    }
}