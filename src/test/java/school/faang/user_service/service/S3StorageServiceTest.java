package school.faang.user_service.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import school.faang.user_service.constants.goal.ImageConstants;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class S3StorageServiceTest {

    private final byte[] bytes = new byte[1024 * 1024];
    private final MockMultipartFile file = createMockFile(bytes);

    @Mock
    private AmazonS3 s3Client;

    @InjectMocks
    private S3StorageService s3Service;

    @Value("${s3.otherBuckets}")
    private String bucketName;

    @Test
    void testPositiveUploadFile() {
        String result = s3Service.uploadFile(file, file.getName());

        verify(s3Client).putObject(any(PutObjectRequest.class));
        assertEquals(file.getName(), result);
    }

    @Test
    void testPositiveDeleteFile() {
        s3Service.deleteFile(file.getName());

        verify(s3Client).deleteObject(bucketName, file.getName());
    }

    @Test
    void testPositiveGetFile() {
        S3Object mockS3Object = mock(S3Object.class);
        S3ObjectInputStream mockStream = mock(S3ObjectInputStream.class);
        when(s3Client.getObject(bucketName, file.getName())).thenReturn(mockS3Object);
        when(mockS3Object.getObjectContent()).thenReturn(mockStream);

        InputStream result = s3Service.getFile(file.getName());

        verify(s3Client).getObject(bucketName, file.getName());
        assertNotNull(result);
    }

    @Test
    void testPositiveGetTypeWhenFormatCorrect() {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        when(s3Client.getObjectMetadata(bucketName, file.getName())).thenReturn(metadata);

        String result = s3Service.getContentType(file.getName());

        assertEquals(result, metadata.getContentType());
    }

    @Test
    void testPositiveGetTypeDefaultFormat() {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("text/plain");
        when(s3Client.getObjectMetadata(bucketName, file.getName())).thenReturn(metadata);

        String result = s3Service.getContentType(file.getName());

        assertEquals(ImageConstants.DEFAULT_FORMAT, result);
    }

    private MockMultipartFile createMockFile(byte[] imageData) {
        return new MockMultipartFile("image.jpg", "image.jpg", "image/jpeg", imageData);
    }
}
