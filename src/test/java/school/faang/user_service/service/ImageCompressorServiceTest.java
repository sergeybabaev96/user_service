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

    private final int firstFileWidth = 1080;
    private final int firstFileHeight = 1000;
    private final ImageCompressorService compressorService = new ImageCompressorService();

    @Test
    void testNegativeCompressWhenImageInvalid() {
        MockMultipartFile file = createMockFile("not-an-image".getBytes());

        assertThrows(RuntimeException.class, () -> compressorService.compressImage(file, firstFileHeight));
    }

    @Test
    void testPositiveWhenNotNeededResize() throws IOException {
        byte[] imageData = createImage(firstFileWidth, firstFileHeight);
        MockMultipartFile file = createMockFile(imageData);

        MultipartFile result = compressorService.compressImage(file, firstFileWidth);

        assertEquals(file, result);
    }

    @Test
    void testPositiveWhenResizeNeeded() throws IOException {
        int fileWidth = 2000;
        int fileHeight = 1500;
        byte[] imageData = createImage(fileWidth, fileHeight);
        MockMultipartFile file = createMockFile(imageData);

        MultipartFile result = compressorService.compressImage(file, firstFileWidth);

        BufferedImage resizedImage = ImageIO.read(new ByteArrayInputStream(result.getBytes()));
        assertTrue(resizedImage.getWidth() <= firstFileWidth);
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
