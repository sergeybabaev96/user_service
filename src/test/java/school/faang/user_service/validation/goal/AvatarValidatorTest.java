package school.faang.user_service.validation.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.avatar.AvatarConfig;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.validation.avatar.AvatarValidator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AvatarValidatorTest {
    @Mock
    private AvatarConfig avatarConfig;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private AvatarValidator avatarValidator;

    private static final long MAX_SIZE = 5242880;

    @BeforeEach
    public void setUp() {
        when(avatarConfig.getMaxSizeBytes()).thenReturn(MAX_SIZE);
    }

    @DisplayName("Проверка валидации аватара с размером меньше максимального")
    @Test
    void givenValidData_WhenCheckAvatarSize_ThenSuccess() {
        when(multipartFile.getSize()).thenReturn(MAX_SIZE - 1);

        assertDoesNotThrow(() -> avatarValidator.checkAvatarSize(multipartFile));
    }

    @DisplayName("Проверка валидации аватара с максимально допустимым размером")
    @Test
    void givenMAxSize_WhenCheckAvatarSize_ThenSuccess() {
        when(multipartFile.getSize()).thenReturn(MAX_SIZE);

        assertDoesNotThrow(() -> avatarValidator.checkAvatarSize(multipartFile));
    }

    @DisplayName("Проверка валидации аватара с превышением максимального размера")
    @Test
    void givenInvalidData_WhenCheckAvatarSize_ThenDataValidationException() {
        when(multipartFile.getSize()).thenReturn(MAX_SIZE + 1);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> avatarValidator.checkAvatarSize(multipartFile));
        assertEquals("Avatar size cant be more 5 mb", exception.getMessage());
    }

    @DisplayName("Проверка валидации реального файла с корректным размером")
    @Test
    void givenMultipartFile_WhenCheckAvatarSize_ThenSuccess() {
        byte[] content = new byte[(int) (MAX_SIZE - 1)];
        MultipartFile file = new MockMultipartFile(
                "avatar",
                "test.png",
                "image/png",
                content
        );

        assertDoesNotThrow(() -> avatarValidator.checkAvatarSize(file));
    }
}