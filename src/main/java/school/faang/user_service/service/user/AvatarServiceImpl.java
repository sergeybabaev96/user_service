package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.properties.AvatarProperties;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.dicebear.DiceBearService;
import school.faang.user_service.service.s3.S3Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvatarServiceImpl implements AvatarService {

    private final static String SMALL_SIZE = "small";
    private final static String LARGE_SIZE = "large";

    private final S3Service s3Service;
    private final AvatarProperties avatarProperties;
    private final UserRepository userRepository;
    private final DiceBearService diceBearService;

    @SneakyThrows
    @Override
    public void saveAvatars(long userId, MultipartFile file) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(
                String.format("User with id = %d not found", userId)
        ));
        String largeAvatarId = UUID.randomUUID().toString();
        String smallAvatarId = UUID.randomUUID().toString();
        File largeFile = compressImage(file, avatarProperties.getMaxWidth());
        String largeImageKey = s3Service.uploadToS3(largeFile, avatarProperties.getBucket(), largeAvatarId);
        File smallFile = compressImage(file, avatarProperties.getMaxSmallWidth());
        String smallImageKey = s3Service.uploadToS3(smallFile, avatarProperties.getBucket(), smallAvatarId);
        updateAvatarUser(user, Pair.of(largeImageKey, smallImageKey));
    }

    @Override
    public InputStream getAvatarByUser(long userId, String size) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException(String.format("User with id = %d not found", userId)));
        UserProfilePic userProfilePic = user.getUserProfilePic();
        if (SMALL_SIZE.equals(size)) {
            return s3Service.downloadFile(avatarProperties.getBucket(), userProfilePic.getSmallFileId());
        }
        if (LARGE_SIZE.equals(size)) {
            return s3Service.downloadFile(avatarProperties.getBucket(), userProfilePic.getFileId());
        }
        throw new IllegalArgumentException(String.format("Size = %s doesn't exists", size));
    }

    @Override
    public InputStream getAvatarByKey(String key) {
        return s3Service.downloadFile(avatarProperties.getBucket(), key);
    }

    @Override
    public void deleteAvatarByKey(String key) {
        s3Service.deleteFile(avatarProperties.getBucket(), key);
    }

    @Override
    public void deleteAvatarByUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException(String.format("User with id = %d not found", userId)));
        UserProfilePic userProfilePic = user.getUserProfilePic();
        s3Service.deleteFile(avatarProperties.getBucket(), userProfilePic.getFileId());
        s3Service.deleteFile(avatarProperties.getBucket(), userProfilePic.getSmallFileId());
        updateAvatarUser(user, Pair.of(null, null));
    }

    @Override
    public Pair<String, String> saveRandomAvatarsToS3(User user) {
        String randomSeed = UUID.randomUUID().toString();
        byte[] avatar = diceBearService.getRandomAvatar(randomSeed, avatarProperties.getSize());
        byte[] smallAvatar = diceBearService.getRandomAvatar(randomSeed, avatarProperties.getSmallSize());

        String avatarId = s3Service.uploadToS3(avatar, avatarProperties.getBucket(),
                UUID.randomUUID().toString(), avatarProperties.getContentType());
        String avatarSmallId = s3Service.uploadToS3(smallAvatar, avatarProperties.getBucket(),
                UUID.randomUUID().toString(), avatarProperties.getContentType());

        return Pair.of(avatarId, avatarSmallId);
    }

    private void updateAvatarUser(User user, Pair<String, String> avatarKeys) {
        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(avatarKeys.getLeft());
        userProfilePic.setSmallFileId(avatarKeys.getRight());
        user.setUserProfilePic(userProfilePic);
        userRepository.save(user);
    }

    private File compressImage(MultipartFile file, int size) throws IOException {
        File compressedFile = File.createTempFile("compressed", ".jpg");
        Thumbnails.of(file.getInputStream())
                .size(size, size)
                .toFile(compressedFile);
        return compressedFile;
    }

}
