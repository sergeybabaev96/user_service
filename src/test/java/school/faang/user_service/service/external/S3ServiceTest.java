package school.faang.user_service.service.external;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.S3Exception;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {

    @Mock
    private AmazonS3 amazonS3Client;

    @InjectMocks
    private S3Service s3Service;

    private final String bucketName = "test-bucket";

    @Test
    void getUnexpiredUrl_ShouldReturnPresignedUrl() throws Exception {
        String fileId = "test-file.jpeg";
        URL expectedUrl = new URL("http://localhost/test-file.jpeg");
        when(amazonS3Client.generatePresignedUrl(any(GeneratePresignedUrlRequest.class))).thenReturn(expectedUrl);

        URL actualUrl = s3Service.getUnexpiredUrl(bucketName, fileId);

        assertEquals(expectedUrl.toString(), actualUrl.toString());
        verify(amazonS3Client, times(1)).generatePresignedUrl(any(GeneratePresignedUrlRequest.class));
    }

    @Test
    void getUnexpiredUrl_ShouldThrowS3Exception_WhenGenerationFails() {
        String fileId = "test-file.jpeg";
        doThrow(SdkClientException.class).when(amazonS3Client).generatePresignedUrl(any(GeneratePresignedUrlRequest.class));

        S3Exception exception = assertThrows(S3Exception.class, () -> s3Service.getUnexpiredUrl(bucketName, fileId));
        assertEquals("Error generating presigned URL", exception.getMessage());
    }
}