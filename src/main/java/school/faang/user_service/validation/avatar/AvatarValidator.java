package school.faang.user_service.validation.avatar;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.avatar.AvatarConfig;
import school.faang.user_service.exception.DataValidationException;

import java.util.Objects;

/**
 * Валидатор для проверки аватаров пользователей.
 * <p>
 * Выполняет проверки согласно конфигурации {@link AvatarConfig}:
 * <ul>
 *   <li>Размер файла (не должен превышать {@link AvatarConfig#getMaxSizeBytes()})</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AvatarValidator {
    private final AvatarConfig avatarConfig;

    /**
     * Проверяет размер файла аватара.
     * <p>
     * Сравнивает размер файла с максимально допустимым значением из {@link AvatarConfig}.
     *
     * @param avatar файл аватара для проверки (не может быть null)
     * @throws DataValidationException если размер файла превышает допустимый*
     */
    public void checkAvatarSize(MultipartFile avatar) {
        long avatarSize = avatar.getSize();
        if (avatarSize > avatarConfig.getMaxSizeBytes()) {
            throw new DataValidationException("Avatar size cant be more 5 mb");
        }
    }

    public void checkFileType(MultipartFile avatar) {
        if (!Objects.equals(avatar.getContentType(), "image/jpeg")) {
            throw new DataValidationException("file content type is not jpg");
        }
    }
}
