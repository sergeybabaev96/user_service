package school.faang.user_service.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.avatar.UserAvatarProperties;
import school.faang.user_service.service.TestUserAvatarProperties;

import javax.annotation.processing.FilerException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class S3ServiceTest {

    @Mock
    private AmazonS3 s3Client;

    @InjectMocks
    private S3Service s3Service;

    private UserAvatarProperties avatarProperties;

    private final String fileKey = "testAvatar.jpeg";
    private final byte[] fileContent = "Test content".getBytes();

    @BeforeEach
    void setUp() {
        avatarProperties = TestUserAvatarProperties.createTestProperties();
        s3Service = new S3Service(s3Client, avatarProperties);
    }

    @Test
    void testUploadFileSuccess() throws FilerException {
        InputStream inputStream = new ByteArrayInputStream("Test content".getBytes());
        when(s3Client.doesBucketExistV2(avatarProperties.getBucketName())).thenReturn(true);

        s3Service.uploadFile(inputStream, "testFile.jpg", 1234, "image/jpeg");

        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class));
    }

    @Test
    void testUploadFileWhenBucketNotExists() throws FilerException {
        InputStream inputStream = new ByteArrayInputStream(fileContent);
        when(s3Client.doesBucketExistV2(avatarProperties.getBucketName())).thenReturn(false);

        s3Service.uploadFile(inputStream, fileKey, fileContent.length, "image/png");

        verify(s3Client, times(1)).createBucket(avatarProperties.getBucketName());
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class));
    }

    @Test
    void testUploadFileFail() {
        InputStream inputStream = new ByteArrayInputStream(fileContent);
        when(s3Client.doesBucketExistV2(avatarProperties.getBucketName())).thenReturn(true);
        doThrow(new RuntimeException("S3 error")).when(s3Client).putObject(any(PutObjectRequest.class));

        assertThrows(FilerException.class, () ->
                s3Service.uploadFile(inputStream, fileKey, fileContent.length, "image/png")
        );
    }

    @Test
    void testDownloadFileSuccess() {
        S3Object s3Object = mock(S3Object.class);
        InputStream inputStream = new ByteArrayInputStream(fileContent);
        when(s3Object.getObjectContent()).thenReturn(new S3ObjectInputStream(inputStream, null));
        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(s3Object);

        InputStream result = s3Service.downloadFile(fileKey);
        assertNotNull(result);
    }

    @Test
    void testDeleteFileSuccess() {
        s3Service.deleteFile(fileKey);
        verify(s3Client, times(1)).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void testDownloadFileFail() {
        S3Object s3Object = mock(S3Object.class);
        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(s3Object);

        try (InputStream inputStream = new ByteArrayInputStream(fileContent);
             S3ObjectInputStream streamReturn = new S3ObjectInputStream(inputStream, null)) {

            when(s3Object.getObjectContent()).thenReturn(streamReturn);
            InputStream result = s3Service.downloadFile(fileKey);
            assertNotNull(result);

        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }
}



