package school.faang.user_service.validator.avatar;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.exception.FileSizeExceedLimitException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FileSizeValidatorTest {

    @InjectMocks
    private FileSizeValidator fileSizeValidator;

    @Mock
    private MultipartFile file;

    @Test
    public void testMaxFileSizeThrowException() {
        long maxFileSize = 5 * 1024 * 1024L; // 5 MB
        when(file.getSize()).thenReturn(maxFileSize + 1);

        assertThrows(FileSizeExceedLimitException.class,
                () -> fileSizeValidator.checkMaxFileSize(file, maxFileSize)
        );
    }
}
