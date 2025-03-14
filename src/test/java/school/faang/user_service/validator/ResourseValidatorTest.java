package school.faang.user_service.validator;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import static org.junit.jupiter.api.Assertions.*;

public class ResourseValidatorTest {

    private final ResourseValidator validator = new ResourseValidator();

    @Test
    void validateFileShouldPassForValidFile() {
        MockMultipartFile file = new MockMultipartFile("file", "avatar.png", "image/png", new byte[1024]);
        assertDoesNotThrow(() -> validator.validate(file));
    }

    @Test
    void validateFileShouldThrowForNonImage() {
        MockMultipartFile file = new MockMultipartFile("file", "file.txt", "text/plain", new byte[1024]);
        assertThrows(IllegalArgumentException.class, () -> validator.validate(file));
    }

    @Test
    void validateFileShouldThrowForTooLargeFile() {
        MockMultipartFile file = new MockMultipartFile("file", "big.jpg", "image/jpeg", new byte[6 * 1024 * 1024]);
        assertThrows(IllegalArgumentException.class, () -> validator.validate(file));
    }

    @Test
    void validateFileShouldThrowForMissingExtension() {
        MockMultipartFile file = new MockMultipartFile("file", "file", "image/png", new byte[1024]);
        assertThrows(IllegalArgumentException.class, () -> validator.validate(file));
    }
}