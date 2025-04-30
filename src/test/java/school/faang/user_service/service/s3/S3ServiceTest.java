package school.faang.user_service.service.s3;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.exception.FileModificationException;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@ExtendWith(MockitoExtension.class)
public class S3ServiceTest {

    @Mock
    private AmazonS3 s3Client;

    @InjectMocks
    private S3Service s3Service;

    private final String bucketName = "test-bucket";
    String folder;
    int maxSize;

    @BeforeEach
    void setUp() {
        folder = "avatars";
        maxSize = 1080;
        ReflectionTestUtils.setField(s3Service, "bucketName", bucketName);
    }

    @Test
    void testUploadFileSuccessfully() throws IOException {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "jpg", baos);
        byte[] imageData = baos.toByteArray();

        MockMultipartFile mockImageFile = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", imageData
        );

        String key = s3Service.uploadFile(mockImageFile, folder, maxSize);

        assertTrue(key.startsWith(folder));
        assertTrue(key.contains("test.jpg"));

        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class));
    }

    @Test
    void testUploadFileThrowsExceptionOnIOError() throws IOException {
        MockMultipartFile brokenFile = mock(MockMultipartFile.class);
        when(brokenFile.getInputStream()).thenThrow(new IOException("Test IO Exception"));

        assertThrows(FileModificationException.class, () ->
                s3Service.uploadFile(brokenFile, folder, maxSize)
        );
    }

    @Test
    void testDownloadFileSuccessfully() {
        String key = "avatars/test.jpg";
        InputStream expectedStream = new ByteArrayInputStream("image-data".getBytes());

        S3Object s3Object = mock(S3Object.class);
        when(s3Object.getObjectContent()).thenReturn(new S3ObjectInputStream(expectedStream, null));
        when(s3Client.getObject(bucketName, key)).thenReturn(s3Object);

        InputStream actualStream = s3Service.downloadFile(key);
        assertNotNull(actualStream);

        verify(s3Client, times(1)).getObject(bucketName, key);
    }

    @Test
    void testDownloadFileThrowsExceptionWhenNotFound() {
        String key = "avatars/not_found.jpg";

        when(s3Client.getObject(bucketName, key)).thenThrow(new AmazonS3Exception("File not found"));

        assertThrows(FileModificationException.class, () -> s3Service.downloadFile(key));
    }

    @Test
    void testDeleteFileThrowsException() {
        String key = "avatars/test.jpg";

        doThrow(new AmazonS3Exception("Delete error")).when(s3Client).deleteObject(bucketName, key);

        assertThrows(FileModificationException.class, () -> s3Service.deleteFile(key));
    }
}
