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
import school.faang.user_service.exception.CustomS3Exception;

import java.net.URL;
import java.util.Date;

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
        when(amazonS3Client.doesBucketExistV2(any())).thenReturn(false);
        when(amazonS3Client.putObject(any(), any(), any(), any())).thenThrow(new SdkClientException("Ex"));
        CustomS3Exception exception = assertThrows(CustomS3Exception.class,
                () -> s3Service.uploadToBucket(bucketName, fileName, data, contentType));

        assertEquals("Error working with S3", exception.getMessage());
    }

    @Test
    void testUploadToBucketCorrect() {
        when(amazonS3Client.doesBucketExistV2(any())).thenReturn(true);
        when(amazonS3Client.putObject(any(), any(), any(), any())).thenReturn(null);
        String actualFileName = s3Service.uploadToBucket(bucketName, fileName, data, contentType);

        assertEquals(fileName, actualFileName);

        verify(amazonS3Client, times(1)).putObject(any(), any(), any(), any());
    }

    @Test
    void testGetUnexpiredUrl() throws Exception {
        String expectedUrl = "http://localhost/test-bucket/test-file.txt?X";
        URL mockUrl = new URL(expectedUrl);
        when(amazonS3Client.generatePresignedUrl(any(GeneratePresignedUrlRequest.class))).thenReturn(mockUrl);

        String actualUrl = s3Service.getUnexpiredUrl(bucketName, fileName);

        assertEquals(actualUrl, expectedUrl);
        verify(amazonS3Client, times(1)).generatePresignedUrl(any());
    }

    @Test
    void testGetPresignedUrlForDownload() throws Exception {
        String expectedUrl = "http://localhost/test-bucket/test-file.txt?X";
        URL mockUrl = new URL(expectedUrl);
        Date expirationDate = new Date(System.currentTimeMillis() + 3600_000);
        when(amazonS3Client.generatePresignedUrl(bucketName, fileName, expirationDate)).thenReturn(mockUrl);

        String actualUrl = s3Service.getPresignedUrlForDownload(bucketName, fileName, expirationDate);

        assertEquals(actualUrl, expectedUrl);
        verify(amazonS3Client, times(1)).generatePresignedUrl(bucketName, fileName, expirationDate);
    }
}