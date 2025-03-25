package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.SneakyThrows;
import net.coobird.thumbnailator.Thumbnails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.avatar.UserAvatarProperties;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.s3.S3Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class UserAvatarService {

    private final UserRepository userRepository;
    private final UserAvatarProperties avatarProperties;
    private final S3Service s3Service;


    @SneakyThrows
    public void uploadAvatar(long userId, MultipartFile avatarFile) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User not found"));

        ByteArrayInputStream bigAvatar =
                compressFile(avatarFile, avatarProperties.getSizeMB(), avatarProperties.getBigSide());
        String bigImageKey = "bigAva" + userId;
        s3Service.uploadFile(bigAvatar, bigImageKey, bigAvatar.available(), avatarFile.getContentType());

        ByteArrayInputStream smallAvatar =
                compressFile(avatarFile, avatarProperties.getSizeMB(), avatarProperties.getSmallSide());
        String smallImageKey = "smallAva" + userId;
        s3Service.uploadFile(smallAvatar, smallImageKey, smallAvatar.available(), avatarFile.getContentType());

        setUserProfilePic(user, bigImageKey, smallImageKey);
    }

    private void setUserProfilePic(User user, String bigImageKey, String smallImageKey) {
        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(bigImageKey);
        userProfilePic.setSmallFileId(smallImageKey);
        user.setUserProfilePic(userProfilePic);
        userRepository.save(user);
    }

    public InputStream getAvatarByUserId(long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User not found"));

        UserProfilePic userProfilePic = user.getUserProfilePic();
        if (userProfilePic == null) {
            throw new EntityNotFoundException("User avatar not found");
        }
        String bigAvatarId = userProfilePic.getFileId();
        return  s3Service.downloadFile(bigAvatarId);
    }


    public InputStream getAvatarByKey(String key) {
        return  s3Service.downloadFile(key);
    }


    public void deleteAvatar(long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User not found"));

        UserProfilePic userProfilePic = user.getUserProfilePic();
        if (userProfilePic == null) {
            throw new EntityNotFoundException("User avatar not found");
        }
        s3Service.deleteFile(userProfilePic.getFileId());
        s3Service.deleteFile(userProfilePic.getSmallFileId());

        setUserProfilePic(user, null, null);
    }


    private ByteArrayInputStream compressFile(MultipartFile avatar, int maxSizeMB, int sideSize) throws IOException {

        int maxSizeBytes = maxSizeMB * 1024 * 1024;
        BufferedImage originalImage = ImageIO.read(avatar.getInputStream());
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        if (originalWidth <= sideSize && originalHeight <= sideSize && avatar.getSize() <= maxSizeBytes) {
            return new ByteArrayInputStream(avatar.getBytes());
        }

        int targetWidth = Math.min(sideSize, originalWidth);
        int targetHeight = Math.min(sideSize, originalHeight);

        if (targetWidth <= 0 || targetHeight <= 0) {
            throw new IllegalArgumentException("Image dimensions must not be less than or equal to 0 pixels");
        }

        float quality = 1.0f;
        ByteArrayOutputStream resultFile = new ByteArrayOutputStream();

        while (quality > 0.1f) {
            Thumbnails.of(avatar.getInputStream())
                    .size(sideSize, sideSize)
                    .outputQuality(quality)
                    .toOutputStream(resultFile);

            if (resultFile.size() < maxSizeBytes) {
                break;
            }
            quality -= 0.1f;
        }
        return new ByteArrayInputStream(resultFile.toByteArray());
    }
}
