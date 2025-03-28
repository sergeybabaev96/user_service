package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserViewDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.FileException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.s3.S3Service;
import school.faang.user_service.service.custommultipartfile.CustomMultipartFile;
import school.faang.user_service.validation.user.UserValidator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserValidator userValidator;
    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final UserMapper userMapper;

    @Transactional
    public UserViewDto addUserAvatar(Long userId, MultipartFile avatar) {
        User user = getUser(userId);
        userValidator.checkAvatarSize(avatar);

        String folder = user.getUsername() + user.getId();

        MultipartFile largeAvatar = resizeImage(avatar, 1080);
        MultipartFile smallAvatar = resizeImage(avatar, 170);

        String largeAvatarKey = s3Service.uploadFile(largeAvatar, folder + "/large");
        String smallAvatarKey = s3Service.uploadFile(smallAvatar, folder + "/small");

        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(largeAvatarKey);
        userProfilePic.setSmallFileId(smallAvatarKey);

        user.setUserProfilePic(userProfilePic);
        return userMapper.toViewDto(user);
    }

    public InputStream getUserAvatar(Long userId) {
        User user = getUser(userId);
        UserProfilePic userProfilePic = user.getUserProfilePic();
        InputStream inputStream = s3Service.downloadFile(userProfilePic.getFileId());
        return inputStream;
    }

    @Transactional
    public void deleteUserAvatar(Long userId) {
        User user = getUser(userId);
        UserProfilePic userProfilePic = user.getUserProfilePic();
        s3Service.deleteFile(userProfilePic.getFileId());
        user.setUserProfilePic(null);
    }

    /**
     * Получение пользователя по указанному идентификатору.
     *
     * @param userId Идентификатор пользователя.
     * @return Объект пользователя.
     * @throws DataValidationException Если пользователь с указанным идентификатором не найден.
     */
    public User getUser(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.error("Ошибка: Не удалось найти пользователя с ID {}", userId);
            return new DataValidationException("Пользователь не найден");
        });
    }

    private MultipartFile resizeImage(MultipartFile file, int maxSize) {
        ByteArrayOutputStream os;
        try {
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            BufferedImage resizedImage = Scalr.resize(originalImage, Scalr.Method.QUALITY, Scalr.Mode.AUTOMATIC, maxSize);

            os = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, "jpg", os);
        } catch (IOException e) {
            throw new FileException(e.getMessage());
        }
        return new CustomMultipartFile(file.getOriginalFilename(), os.toByteArray(), file.getContentType());
    }
}
