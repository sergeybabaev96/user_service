package school.faang.user_service.service.externalStorage;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.externalStorage.ExternalResourceDto;
import school.faang.user_service.exception.ExternalServiceError;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class S3ServiceImplTest {
    public final String TEST_BUCKET_NAME = "test-bucket";
    public final String TEST_FOLDER = "test folder";
    public final String TEST_FILENAME = "test filename";
    public final String TEST_FILE_CONTENT = "test content";
    private final String TEST_CONTENT_TYPE = "content type";

    private final int fileContentSize = TEST_FILE_CONTENT.length();
    private final InputStream testFileDataStream = new ByteArrayInputStream(TEST_FILE_CONTENT.getBytes());

    @Mock
    private AmazonS3 s3Client;

    @InjectMocks
    private S3ServiceImpl s3Service;

    @Captor
    ArgumentCaptor<PutObjectRequest> putObjectRequestCaptor;

    @Captor
    ArgumentCaptor<DeleteObjectRequest> deleteObjectRequestCaptor;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(s3Service, "bucketName", TEST_BUCKET_NAME);
    }

    @Test
    public void testUploadFile_S3ClientThrows_Throws() {
        doThrow(new RuntimeException("S3 ошибка")).when(s3Client).putObject(any());

        assertThrows(ExternalServiceError.class, () -> runUploadFile(TEST_CONTENT_TYPE));
    }

    @Test
    public void testUploadFile_UseDefaultContentTypeAndS3ClientSuccess_ReturnsExternalResourceDto() {
        // Act
        var result = runUploadFile(null);

        // Assert
        verify(s3Client).putObject(putObjectRequestCaptor.capture());
        var capturedRequest = putObjectRequestCaptor.getValue();

        assertEquals(MediaType.ALL_VALUE, capturedRequest.getMetadata().getContentType());
        assertEquals(MediaType.ALL_VALUE, result.contentType());
        assertEquals(capturedRequest.getKey(), result.key());
    }

    @Test
    public void testUploadFile_ContentTypePresentAndS3ClientSuccess_ReturnsExternalResourceDto() {
        // Act
        var result = runUploadFile(TEST_CONTENT_TYPE);

        // Assert
        verify(s3Client).putObject(putObjectRequestCaptor.capture());
        var capturedRequest = putObjectRequestCaptor.getValue();

        assertEquals(TEST_BUCKET_NAME, capturedRequest.getBucketName());
        assertEquals(fileContentSize, capturedRequest.getMetadata().getContentLength());
        assertEquals(TEST_CONTENT_TYPE, capturedRequest.getMetadata().getContentType());
        assertEquals(capturedRequest.getKey(), result.key());

        assertEquals(BigInteger.valueOf(fileContentSize), result.size());
        assertEquals(TEST_CONTENT_TYPE, result.contentType());
        assertEquals(TEST_FILENAME, result.name());
    }

    @Test
    public void testDeleteFile_S3ClientThrows_Throws() {
        var key = "test-key";
        doThrow(new RuntimeException("S3 ошибка")).when(s3Client).deleteObject(any());

        assertThrows(ExternalServiceError.class, () -> s3Service.deleteFile(key));
    }

    @Test
    public void testDeleteFile_S3ClientSuccess_Success() {
        // Arrange
        var key = "test-key";

        // Act
        s3Service.deleteFile(key);

        // Assert
        verify(s3Client).deleteObject(deleteObjectRequestCaptor.capture());
        var capturedRequest = deleteObjectRequestCaptor.getValue();

        assertEquals(TEST_BUCKET_NAME, capturedRequest.getBucketName());
        assertEquals(key, capturedRequest.getKey());
    }

    private ExternalResourceDto runUploadFile(String TEST_CONTENT_TYPE) {
        return s3Service.uploadFile(
                testFileDataStream,
                fileContentSize,
                TEST_CONTENT_TYPE,
                TEST_FILENAME,
                TEST_FOLDER);
    }
}