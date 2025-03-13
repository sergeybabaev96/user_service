package school.faang.user_service.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class S3ServiceTest {

    private AmazonS3 amazonS3;
    private S3Service s3Service;

    @BeforeEach
    void setup() throws Exception {
        amazonS3 = mock(AmazonS3.class);
        s3Service = new S3Service(amazonS3);

        Field field = S3Service.class.getDeclaredField("bucketName");
        field.setAccessible(true);
        field.set(s3Service, "test-bucket");
    }

    @Test
    void uploadFile_shouldCallPutObject() {
        ByteArrayInputStream stream = new ByteArrayInputStream("test".getBytes());
        s3Service.uploadFile("test.png", stream, 4, "image/png");

        verify(amazonS3, times(1)).putObject(any());
    }

    @Test
    void deleteFile_shouldCallDeleteObject() {
        String fileId = "test.png";
        s3Service.deleteFile(fileId);

        verify(amazonS3, times(1)).deleteObject(eq("test-bucket"), eq(fileId));
    }

    @Test
    void generatePresignedUrl_shouldCallGeneratePresignedUrl() throws Exception {
        String fileId = "test.png";
        URL mockUrl = new URL("http://mock-url.com/test.png");

        when(amazonS3.generatePresignedUrl(any(GeneratePresignedUrlRequest.class))).thenReturn(mockUrl);

        String result = s3Service.generatePresignedUrl(fileId);

        assertEquals("http://mock-url.com/test.png", result);
        verify(amazonS3, times(1)).generatePresignedUrl(any(GeneratePresignedUrlRequest.class));
    }
}