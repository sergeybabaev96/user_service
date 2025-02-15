package school.faang.user_service.service.user;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.client.avatar.AvatarFeignClient;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DownloadMinioException;
import school.faang.user_service.properties.AvatarProperties;
import school.faang.user_service.repository.user.UserRepository;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvatarServiceImpl implements AvatarService {
    private final S3Service s3Service;
    private final AvatarFeignClient avatarFeignClient;
    private final AvatarProperties avatarProperties;
    private final UserRepository userRepository;

    @Getter
    private byte[] defaultAvatar;

    @PostConstruct
    public void init() {
        try (InputStream object = s3Service.downloadFile(avatarProperties.getBucket(),
                avatarProperties.getDefaultAvatar())) {
            defaultAvatar = object.readAllBytes();
        } catch (IOException e) {
            log.error("Download default avatar failed.", e);
            throw new DownloadMinioException("Download default avatar failed");
        }
    }

    @SneakyThrows
    @Override
    public void saveAvatars(long userId, MultipartFile file) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(
                String.format("User with id = %d not found", userId)
        ));
        String largeAvatarId = UUID.randomUUID().toString();
        String smallAvatarId = UUID.randomUUID().toString();
        File largeFile = compressImage(file, 1080);
        String largeImageKey = s3Service.uploadToS3(largeFile, avatarProperties.getBucket(), largeAvatarId);
        File smallFile = compressImage(file, 170);
        String smallImageKey = s3Service.uploadToS3(smallFile, avatarProperties.getBucket(), smallAvatarId);

        updateAvatarUser(user, Pair.of(largeImageKey, smallImageKey));
    }

    @Override
    public InputStream getAvatar(String key) {
        return s3Service.downloadFile(avatarProperties.getBucket(), key);
    }

    @Override
    public void deleteAvatar(String key) {
        s3Service.deleteFile(avatarProperties.getBucket(), key);
    }

    @Override
    public Pair<String, String> saveRandomAvatarsToS3(User user) {
        String randomSeed = UUID.randomUUID().toString();
        byte[] avatar = getAvatarFromDiceBear(randomSeed, avatarProperties.getSize());
        byte[] smallAvatar = getAvatarFromDiceBear(randomSeed, avatarProperties.getSmallSize());

        String avatarId = s3Service.uploadToS3(avatar, avatarProperties.getBucket(),
                UUID.randomUUID().toString(), avatarProperties.getContentType());
        String avatarSmallId = s3Service.uploadToS3(smallAvatar, avatarProperties.getBucket(),
                UUID.randomUUID().toString(), avatarProperties.getContentType());

        return Pair.of(avatarId, avatarSmallId);
    }

    private byte[] getAvatarFromDiceBear(String seed, int size) {
        try {
            return avatarFeignClient.getAvatar(seed, size);
        } catch (Exception e) {
            log.warn("Failed to get avatar from from DiceBear. Use default avatar.");
            return defaultAvatar;
        }
    }

    private User updateAvatarUser(User user, Pair<String, String> avatars) {
        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(avatars.getLeft());
        userProfilePic.setSmallFileId(avatars.getRight());

        user.setUserProfilePic(userProfilePic);
        return userRepository.save(user);
    }

    private File compressImage(MultipartFile file, int size) throws IOException {
        File compressedFile = File.createTempFile("compressed", ".jpg");
        Thumbnails.of(file.getInputStream())
                .size(size, size)
                .toFile(compressedFile);
        return compressedFile;
    }

}
