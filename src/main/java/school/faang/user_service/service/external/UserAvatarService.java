package school.faang.user_service.service.external;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.AppConfig;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.FileTypeIncorrectException;
import school.faang.user_service.exception.UserProfileWasNotFound;
import school.faang.user_service.exception.UserWasNotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.external.abs.AvatarUploadingSystem;

import java.io.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAvatarService {
    private final UserRepository userRepository;
    private final AvatarUploadingSystem avatarUploadingSystem;
    private final AppConfig appConfig;

    @Transactional
    public UserProfilePic uploadAvatar(Long userId, MultipartFile multipartFile) {
        checkFileType(multipartFile.getContentType());
        User user = findUserById(userId);
        int largeFileSize = appConfig.getAvatarLargeFileSize();
        int smallFileSize = appConfig.getAvatarSmallFileSize();
        String contentType = multipartFile.getContentType();

        ByteArrayOutputStream largeStream = new ByteArrayOutputStream();
        ByteArrayOutputStream smallStream = new ByteArrayOutputStream();

        try {
            avatarUploadingSystem.resizeImage(multipartFile, largeFileSize, largeStream);
            avatarUploadingSystem.resizeImage(multipartFile, smallFileSize, smallStream);

            String largeFileId = avatarUploadingSystem.uploadToMinio(largeStream, contentType);
            String smallFileId = avatarUploadingSystem.uploadToMinio(smallStream, contentType);

            UserProfilePic userProfilePic = new UserProfilePic();
            userProfilePic.setFileId(largeFileId);
            userProfilePic.setSmallFileId(smallFileId);

            user.setUserProfilePic(userProfilePic);

            return userProfilePic;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return new UserProfilePic();
    }

    @Transactional(readOnly = true)
    public byte[] getProfilePicture(Long userId) {
        User user = findUserById(userId);

        UserProfilePic userProfilePic = user.getUserProfilePic();
        if (userProfilePic == null || userProfilePic.getFileId() == null) {
            throw new UserProfileWasNotFound("Profile picture not found -> ID : " + userId);
        }

        try {
            return avatarUploadingSystem.getImageFromMinio(userProfilePic.getFileId());
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return new byte[0];
    }

    @Transactional
    public void deleteProfilePic(Long userId) {
        User user = findUserById(userId);
        UserProfilePic profilePic = user.getUserProfilePic();
        if (profilePic == null) {
            return;
        }

        avatarUploadingSystem.deleteFile(profilePic.getFileId());
        avatarUploadingSystem.deleteFile(profilePic.getSmallFileId());

        profilePic.setFileId(null);
        profilePic.setSmallFileId(null);
    }

    private void checkFileType(String contentType) {
        if (contentType == null || !contentType.startsWith("image/")) {
            log.error("Only image files are allowed. Provided: {}, ", contentType);
            throw new FileTypeIncorrectException("");
        }
    }
    
    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserWasNotFoundException("User was not found -> ID : " + userId));
    }
}
