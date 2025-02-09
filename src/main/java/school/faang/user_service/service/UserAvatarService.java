package school.faang.user_service.service;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.avatar.AvatarResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.entity.avatar.AvatarType;
import school.faang.user_service.service.external.DiceBearService;
import school.faang.user_service.service.external.S3Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Validated
@Service
public class UserAvatarService {
    private final S3Service s3Service;
    private final DiceBearService diceBearService;
    private final UserService userService;

    @Value("${avatar.bucketName}")
    private String bucketName;

    @Value("${avatar.customAvatars}")
    private String customAvatarsStorage;

    @Value("#{${avatar.maxImageSize}}")
    private int maxImageSize;

    @Value("${avatar.maxLargeImageSideSize}")
    private int largeImageSideSize;

    @Value("${avatar.maxSmallImageSideSize}")
    private int smallImageSideSize;

    @Transactional
    public User generateRandomAvatar(@NotNull User user, AvatarType type) {
        String randomName = UUID.randomUUID().toString();
        byte[] avatarData = diceBearService.generateAvatar(randomName, type);
        String avatarNameAndExtension = randomName + type.getExtension();

        String fileId = s3Service.uploadToBucket(bucketName, avatarNameAndExtension, avatarData, type.getContentType());
        return setUploadedAvatar(user, fileId);
    }

    @Transactional(readOnly = true)
    public String getUserAvatar(@NotNull User user) {
        String fileId = Optional.ofNullable(user.getUserProfilePic())
                .map(UserProfilePic::getFileId)
                .filter(id -> !id.isBlank())
                .orElseThrow(() -> new NoSuchElementException("No avatar for user " + user.getId()));

        return s3Service.getUnexpiredUrl(bucketName, fileId);
    }

    @Transactional
    private User setUploadedAvatar(User user, String url) {
        UserProfilePic newPic = new UserProfilePic();
        newPic.setFileId(url);
        user.setUserProfilePic(newPic);
        return user;
    }

    @Transactional
    public AvatarResponseDto uploadAvatar(Long userId, MultipartFile file) {
        User user = userService.getUser(userId);

        if (file.getSize() > maxImageSize) {
            throw new IllegalArgumentException(String.format("File size must not be more than %d Mb", maxImageSize));
        }

        try (InputStream inputStream = file.getInputStream()) {
            BufferedImage originalImage = ImageIO.read(inputStream);
            if (originalImage == null) {
                throw new IllegalArgumentException("Invalid image format");
            }

            byte[] largeImageBytes = processImage(originalImage, largeImageSideSize);
            byte[] smallImageBytes = processImage(originalImage, smallImageSideSize);

            String largeImageKey = String.format("%s.jpg", UUID.randomUUID());
            String smallImageKey = String.format("%s.jpg", UUID.randomUUID());

            s3Service.uploadToBucket(customAvatarsStorage, largeImageKey, largeImageBytes, "image/jpeg");
            s3Service.uploadToBucket(customAvatarsStorage, smallImageKey, smallImageBytes, "image/jpeg");

            UserProfilePic profilePic = new UserProfilePic();
            profilePic.setFileId(largeImageKey);
            profilePic.setSmallFileId(smallImageKey);
            user.setUserProfilePic(profilePic);
            log.info("User #{} upload new avatar", userId);

            return AvatarResponseDto.builder()
                    .userId(userId)
                    .smallImageKey(smallImageKey)
                    .largeImageKey(largeImageKey)
                    .build();

        } catch (IOException e) {
            throw new RuntimeException("Error processing image", e);
        }
    }

    @Transactional(readOnly = true)
    public String getAvatar(Long userId) {
        UserProfilePic profilePic = isUserHasAvatar(userId);

       return s3Service.getUnexpiredUrl(customAvatarsStorage, profilePic.getFileId());
    }

    @Transactional
    public void deleteAvatar(Long userId) {
        UserProfilePic profilePic = isUserHasAvatar(userId);
        s3Service.deleteImageFromBucket(customAvatarsStorage, profilePic.getFileId());
        s3Service.deleteImageFromBucket(customAvatarsStorage, profilePic.getSmallFileId());
        User user = userService.getUser(userId);
        user.setUserProfilePic(null);
        log.info("User with ID#{} delete avatar", userId);
    }

    private byte[] bufferedImageToByteArray(BufferedImage image) {
        try (ByteArrayOutputStream outputByteStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "jpg", outputByteStream);
            return outputByteStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error processing image", e);
        }
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int maxSize) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        if (width <= maxSize && height <= maxSize) {
            return originalImage;
        }

        return Scalr.resize(originalImage, Scalr.Method.QUALITY, maxSize);
    }

    private UserProfilePic isUserHasAvatar(Long userId) {
        User user = userService.getUser(userId);
        UserProfilePic profilePic = user.getUserProfilePic();

        if (profilePic == null) {
            throw new NoSuchElementException(String.format("User with ID#%d has not set an avatar yet", userId));
        }

        return profilePic;
    }

    private byte[] processImage(BufferedImage image, int sideSize) {
        BufferedImage resizedImage = resizeImage(image, sideSize);
        return bufferedImageToByteArray(resizedImage);
    }
}