package school.faang.user_service.service.avatar;

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
import school.faang.user_service.validator.avatar.FileSizeValidator;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class AvatarService {

    @Value("${user-avatar.max-size-bytes}")
    private long permittedSize;
    @Value("${user-avatar.sizes.small}")
    private int smallerSize;
    @Value("${user-avatar.sizes.large}")
    private int largerSize;

    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final FileSizeValidator fileSizeValidator;
    private final UserService userService;

    @Transactional
    public void addUserAvatar(Long userId, MultipartFile file) {
        User user = userService.getUserFromDb(userId);
        fileSizeValidator.checkMaxFileSize(file, permittedSize);
        String folder = userId + "_user_avatars";
        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(s3Service.uploadFile(file, folder, largerSize));
        userProfilePic.setSmallFileId(s3Service.uploadFile(file, folder, smallerSize));
        user.setUserProfilePic(userProfilePic);
        userRepository.save(user);
    }

    public InputStream getUserAvatar(Long userId) {
        User user = userService.getUserFromDb(userId);
        UserProfilePic userProfilePic = user.getUserProfilePic();
        String avatarKey = userProfilePic.getFileId();
        return s3Service.downloadFile(avatarKey);
    }

    @Transactional
    public void removeUserAvatar(Long userId) {
        User user = userService.getUserFromDb(userId);
        UserProfilePic userProfilePic = user.getUserProfilePic();
        s3Service.deleteFile(userProfilePic.getFileId());
        s3Service.deleteFile(userProfilePic.getSmallFileId());
        user.setUserProfilePic(null);
        userRepository.save(user);
    }

}
