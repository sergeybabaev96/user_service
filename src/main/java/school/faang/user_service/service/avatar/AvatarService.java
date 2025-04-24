package school.faang.user_service.service.avatar;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.UserService;
import school.faang.user_service.service.s3.S3Service;
import school.faang.user_service.validator.avatar.AvatarValidator;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class AvatarService {

    @Value("${user-avatar.max-size-bytes}")
    @NotNull
    private long permittedSize;
    @Value("${user-avatar.sizes.small}")
    @NotNull
    private int smallerSize;
    @Value("${user-avatar.sizes.large}")
    @NotNull
    private int largerSize;

    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final AvatarValidator avatarValidator;
    private final UserService userService;

    @Transactional
    public void addUserAvatar(@NotNull Long userId, @NotNull MultipartFile file) {
        User user = userService.getUserFromDb(userId);
        avatarValidator.checkMaxFileSize(file, permittedSize);
        String folder = userId + "_user_avatars";
        UserProfilePic userProfilePic = user.getUserProfilePic();
        if (userProfilePic == null) {
            userProfilePic = new UserProfilePic();
        }
        userProfilePic.setFileId(s3Service.uploadFile(file, folder, largerSize));
        userProfilePic.setSmallFileId(s3Service.uploadFile(file, folder, smallerSize));
        user.setUserProfilePic(userProfilePic);
        userRepository.save(user);
    }

    public InputStream getUserAvatar(@NotNull Long userId, Boolean isSmall) {
        User user = userService.getUserFromDb(userId);
        UserProfilePic userProfilePic = user.getUserProfilePic();
        String avatarKey;
        if (isSmall) {
            avatarKey = userProfilePic.getSmallFileId();
        } else {
            avatarKey = userProfilePic.getFileId();
        }
        avatarValidator.checkAvatarKey(avatarKey);
        return s3Service.downloadFile(avatarKey);

    }

    @Transactional
    public void removeUserAvatar(@NotNull Long userId) {
        User user = userService.getUserFromDb(userId);
        UserProfilePic userProfilePic = user.getUserProfilePic();
        if (userProfilePic != null) {
            s3Service.deleteFile(userProfilePic.getFileId());
            s3Service.deleteFile(userProfilePic.getSmallFileId());
        }
        user.setUserProfilePic(null);
        userRepository.save(user);
    }

}
