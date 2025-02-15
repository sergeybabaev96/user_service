package school.faang.user_service.service.user;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3ServiceImplTest {

    @Mock
    private AmazonS3 s3Client;

    @InjectMocks
    private S3ServiceImpl s3Service;

    @Test
    public void testUpload() {
        byte[] image = "image".getBytes(StandardCharsets.UTF_8);
        PutObjectResult mock = mock(PutObjectResult.class);
        when(s3Client.putObject(any())).thenReturn(mock);

        String s = s3Service.uploadToS3(image, "bucket", "key", "content-type");

        assertNotNull(s);
    }

    @Test
    public void testDownload() {
        S3Object mockS3Object = mock(S3Object.class);
        S3ObjectInputStream mockInputStream = new S3ObjectInputStream(
                new ByteArrayInputStream("test data".getBytes()), null);
        when(mockS3Object.getObjectContent()).thenReturn(mockInputStream);
        when(s3Client.getObject(any())).thenReturn(mockS3Object);

        InputStream inputStream = s3Service.downloadFile("bucket", "key");

        assertNotNull(inputStream);
    }

}