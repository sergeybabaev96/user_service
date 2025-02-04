package school.faang.user_service.service.user;

import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MinioServiceImplTest {

    @Mock
    private MinioClient minioClient;

    @InjectMocks
    private MinioServiceImpl minioService;

    @Test
    public void testUpload() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        byte[] image = "image".getBytes(StandardCharsets.UTF_8);
        ByteArrayResource byteArrayResource = new ByteArrayResource(image);
        ObjectWriteResponse mock = mock(ObjectWriteResponse.class);
        when(minioClient.putObject(any())).thenReturn(mock);

        String s = minioService.uploadToMinio(byteArrayResource, "bucket");

        assertNotNull(s);
    }

    @Test
    public void testDownload() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        GetObjectResponse mock = mock(GetObjectResponse.class);
        when(minioClient.getObject(any())).thenReturn(mock);

        InputStream inputStream = minioService.downloadFile("bucket", "key");

        assertNotNull(inputStream);
    }

}