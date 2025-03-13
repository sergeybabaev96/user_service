package school.faang.user_service.service.resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;

import static org.junit.jupiter.api.Assertions.*;

public class ResourseServiceTest {

    private ResourseService resourseService;

    @BeforeEach
    void setUp() {
        resourseService = new ResourseService();
    }

    @Test
    void resizeImage_shouldReturnResizedBytes() throws Exception {
        BufferedImage image = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);
        byte[] imageBytes = outputStream.toByteArray();

        byte[] result = resourseService.resize(imageBytes, 100, "png");

        assertNotNull(result);
        assertTrue(result.length > 0);
    }
}