package school.faang.user_service.service.imageresize;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.exception.FileException;
import school.faang.user_service.service.avatar.ImageResize;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ImageResizeTest {
    private ImageResize imageResize;

    @BeforeEach
    public void setUp() {
        imageResize = new ImageResize();
    }

    @DisplayName("Проверка успешного изменения размера изображения при передаче валидных данных")
    @Test
    public void givenValidData_WhenResizeImage_ThenSuccess() throws IOException {
        BufferedImage originalImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(originalImage, "jpg", outputStream);
        byte[] imageBytes = outputStream.toByteArray();

        MultipartFile file = new MockMultipartFile("image.jpg", "image.jpg", "image/jpeg", imageBytes);

        MultipartFile resizedFile = imageResize.resizeImage(file, 100);
        BufferedImage resizedImage = ImageIO.read(new ByteArrayInputStream(resizedFile.getBytes()));

        assertNotNull(resizedFile);
        assertNotNull(resizedImage);
        assertTrue(resizedImage.getWidth() <= 100 || resizedImage.getHeight() <= 100);
    }

    @DisplayName("Проверка получения ошибки при изменении размера невалидного изображения")
    @Test
    public void givenValidData_WhenResizeImage_ThenException() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenThrow(new IOException("Error reading file"));

        assertThrows(FileException.class, () -> imageResize.resizeImage(file, 100));
    }
}