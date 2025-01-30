package school.faang.user_service.service.external;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {

    @Mock
    private AmazonS3 amazonS3Client;

    @InjectMocks
    private S3Service s3Service;

    private String bucketName;
    private String fileName;
    private byte[] fileData;
    private String contentType;

    @BeforeEach
    void setUp() {
        bucketName = "test-bucket";
        fileName = "test-file.jpg";
        fileData = "dummy data".getBytes();
        contentType = "image/jpeg";
    }

    @Test
    void testGetUnexpiredUrl_Success() throws Exception {
        String expectedUrl = "http://example.com/s3-url";
        URL mockUrl = new URL(expectedUrl);
        when(amazonS3Client.generatePresignedUrl(any(GeneratePresignedUrlRequest.class))).thenReturn(mockUrl);

        String actualUrl = s3Service.getUnexpiredUrl(fileName);

        assertEquals(expectedUrl, actualUrl);
        verify(amazonS3Client, times(1)).generatePresignedUrl(any(GeneratePresignedUrlRequest.class));
    }

    @Test
    void testGetUnexpiredUrl_S3Exception() {
        when(amazonS3Client.generatePresignedUrl(any(GeneratePresignedUrlRequest.class)))
                .thenThrow(SdkClientException.class);

        S3Exception exception = assertThrows(S3Exception.class, () ->
                s3Service.getUnexpiredUrl(fileName)
        );

        assertEquals("Error generating presigned URL", exception.getMessage());
        verify(amazonS3Client, times(1)).generatePresignedUrl(any(GeneratePresignedUrlRequest.class));
    }
}
