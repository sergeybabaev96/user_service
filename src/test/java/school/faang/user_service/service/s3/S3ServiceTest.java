package school.faang.user_service.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.exception.FileException;
import school.faang.user_service.s3.S3Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class S3ServiceTest {
    @Mock
    private AmazonS3 s3Client;

    @InjectMocks
    private S3Service s3Service;

    private MultipartFile file;
    String fileId;

    @BeforeEach
    public void setUp() throws IOException {
        fileId = "file";

        BufferedImage image =
                new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        file = new MockMultipartFile("file", "test.jpg", "image/jpg", imageBytes);
    }

    @DisplayName("Проверка успешной загрузки файла при передаче валидных данных")
    @Test
    public void givenValidData_WhenUploadFile_ThenSuccess() {
        when(s3Client.putObject(any(PutObjectRequest.class))).thenReturn(new PutObjectResult());

        assertNotNull(s3Service.uploadFile(file, "murloc"));
    }

    @DisplayName("Проверка получения ошибки при загрузке файла с невалидными данными")
    @Test
    public void givenValidData_WhenUploadFile_ThenFileException() {
        when(s3Client.putObject(any(PutObjectRequest.class))).thenThrow(FileException.class);

        assertThrows(FileException.class,
                () -> s3Service.uploadFile(file, "murloc"));
    }

    @DisplayName("Проверка успешного скачивания файла при передаче валидных данных")
    @Test
    public void givenValidData_WhenDownloadFile_ThenSuccess() {
        S3Object s3Object = new S3Object();

        when(s3Client.getObject(null, fileId)).thenReturn(s3Object);

        assertNotNull(s3Service.downloadFile(fileId));
    }

    @DisplayName("Проверка получения ошибки при скачивании несуществующего файла")
    @Test
    public void givenValidData_WhenDownloadFile_ThenFileException() {
        when(s3Client.getObject(null, fileId)).thenThrow(FileException.class);

        assertThrows(FileException.class, () -> s3Service.downloadFile(fileId));
    }

    @DisplayName("Проверка успешного удаления файла при передаче валидных данных")
    @Test
    public void givenValidData_WhenDeleteFile_ThenSuccess() {
        Assertions.assertDoesNotThrow(() -> s3Service.deleteFile(fileId));
    }

    @DisplayName("Проверка получения ошибки при удалении несуществующего файла")
    @Test
    public void givenValidData_WhenDeleteFile_ThenFileException() {
        doThrow(FileException.class).when(s3Client).deleteObject(any());

        assertThrows(FileException.class, () -> s3Service.deleteFile(fileId));
    }
}