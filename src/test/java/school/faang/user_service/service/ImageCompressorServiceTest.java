package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class ImageCompressorServiceTest {

    private final ImageCompressorService compressorService = new ImageCompressorService();

    @Test
    void testNegativeCompressWhenImageInvalid() {
        MockMultipartFile file = createMockFile("not-an-image".getBytes());

        assertThrows(RuntimeException.class, () -> compressorService.compressImage(file, 1000));
    }

    @Test
    void testPositiveWhenNotNeededResize() throws IOException {
        byte[] imageData = createImage(1080, 1000);
        MockMultipartFile file = createMockFile(imageData);

        MultipartFile result = compressorService.compressImage(file, 1080);

        assertEquals(file, result);
    }

    @Test
    void testPositiveWhenResizeNeeded() throws IOException {
        byte[] imageData = createImage(2000, 1500);
        MockMultipartFile file = createMockFile(imageData);

        MultipartFile result = compressorService.compressImage(file, 1080);

        BufferedImage resizedImage = ImageIO.read(new ByteArrayInputStream(result.getBytes()));
        assertTrue(resizedImage.getWidth() <= 1080);
        assertEquals(file.getName(), result.getOriginalFilename());
    }

    private byte[] createImage(int width, int height) throws IOException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        try (var output = new java.io.ByteArrayOutputStream()) {
            ImageIO.write(image, "jpg", output);
            return output.toByteArray();
        }
    }

    private MockMultipartFile createMockFile(byte[] imageData) {
        return new MockMultipartFile("image.jpg", "image.jpg", "image/jpeg", imageData);
    }
}
