package school.faang.user_service.service.avatar;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.avatar.AvatarConfig;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.s3.S3Service;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validation.avatar.AvatarValidator;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Сервис для работы с аватарами пользователей.
 * <p>
 * Предоставляет функционал для загрузки, получения и удаления аватаров пользователей.
 * Автоматически создает две версии аватара - большую и маленькую, сохраняя их в S3 хранилище.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class AvatarService {
    private final String SMALL_AVATAR_SIZE = "small";
    private final String LARGE_AVATAR_SIZE = "large";

    private final AvatarConfig avatarConfig;
    private final S3Service s3Service;
    private final UserService userService;
    private final AvatarValidator avatarValidator;
    private final ImageResize imageResize;

    /**
     * Добавляет аватар пользователя.
     * <p>
     * Процесс добавления аватара:
     * 1. Проверяет валидность размера файла
     * 2. Создает папку пользователя по шаблону
     * 3. Генерирует две версии аватара (большую и маленькую)
     * 4. Загружает обе версии в S3 хранилище
     * 5. Сохраняет ссылки на аватары в профиле пользователя
     * </p>
     *
     * @param userId ID пользователя
     * @param avatar файл аватара
     * @throws IllegalArgumentException если файл аватара не проходит валидацию
     */
    @Transactional
    public void addUserAvatar(long userId, MultipartFile avatar) {
        User user = userService.getUser(userId);
        avatarValidator.checkAvatarSize(avatar);

        String folder = buildUserFolder(user);

        Map<String, MultipartFile> resizedAvatars = resizeAvatar(avatar);

        Map<String, String> uploadedAvatarsKey = uploadAvatars(resizedAvatars, folder);

        UserProfilePic profilePic = new UserProfilePic();
        String largeKey = uploadedAvatarsKey.get(LARGE_AVATAR_SIZE);
        String smallKey = uploadedAvatarsKey.get(SMALL_AVATAR_SIZE);

        profilePic.setFileId(largeKey);
        profilePic.setSmallFileId(smallKey);

        user.setUserProfilePic(profilePic);
    }

    /**
     * Удаляет аватар пользователя.
     * <p>
     * Удаляет обе версии аватара (большую и маленькую) из S3 хранилища
     * и очищает ссылки в профиле пользователя.
     * </p>
     *
     * @param userId ID пользователя
     */
    @Transactional
    public void deleteUserAvatar(long userId) {
        User user = userService.getUser(userId);
        UserProfilePic userProfilePic = user.getUserProfilePic();
        s3Service.deleteFile(userProfilePic.getFileId());
        s3Service.deleteFile(userProfilePic.getSmallFileId());
        user.setUserProfilePic(null);
    }

    /**
     * Получает аватар пользователя.
     *
     * @param userId ID пользователя
     * @return ресурс с аватаром пользователя
     */
    public Resource getUserAvatar(long userId) {
        User user = userService.getUser(userId);
        UserProfilePic userProfilePic = user.getUserProfilePic();

        return s3Service.downloadFile(userProfilePic.getFileId());
    }

    /**
     * Строит путь к папке пользователя для хранения аватаров.
     * <p>
     * Использует шаблон из конфигурации, подставляя имя пользователя.
     * </p>
     *
     * @param user пользователь
     * @return путь к папке пользователя
     */
    private String buildUserFolder(User user) {
        return avatarConfig.getFolderTemplate().replace("{username}", user.getUsername());
    }

    /**
     * Создает две версии аватара (большую и маленькую) согласно конфигурации.
     *
     * @param avatar оригинальный файл аватара
     * @return карта с измененными размерами аватаров (ключи: "small", "large")
     */
    private Map<String, MultipartFile> resizeAvatar(MultipartFile avatar) {
        return avatarConfig.getSizes().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> imageResize.resizeImage(avatar, entry.getValue())
                ));
    }

    /**
     * Загружает аватары в S3 хранилище.
     *
     * @param avatars    карта с аватарами разных размеров
     * @param folderPath путь к папке пользователя
     * @return карта с ключами загруженных файлов в S3 (ключи: "small", "large")
     */
    private Map<String, String> uploadAvatars(Map<String, MultipartFile> avatars,
                                              String folderPath) {
        Map<String, String> result = new HashMap<>();
        avatars.forEach((sizeName, file) -> {
            String fullPath = folderPath + "/" + sizeName;
            String fileKey = s3Service.uploadFile(file, fullPath);
            result.put(sizeName, fileKey);
        });
        return result;
    }
}
