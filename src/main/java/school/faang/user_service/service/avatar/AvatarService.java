package school.faang.user_service.service.avatar;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.avatar.DeleteAvatarResponse;
import school.faang.user_service.dto.avatar.GetAvatarResponse;
import school.faang.user_service.dto.avatar.UploadAvatarResponse;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.service.UserService;
import school.faang.user_service.service.resource.ResourseService;
import school.faang.user_service.service.s3.S3Service;
import school.faang.user_service.validator.ResourseValidator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvatarService {

    private final UserService userService;
    private final S3Service s3Service;
    private final ResourseService resourseService;
    private final ResourseValidator resourseValidator;

    @Transactional
    public UploadAvatarResponse uploadAvatar(Long userId, MultipartFile file) {
        resourseValidator.validate(file);
        String format = resourseValidator.getFileExtension(file);

        String filePrefix = String.format("avatars/%d/%s", userId, UUID.randomUUID());
        String fileId = filePrefix + "." + format;
        String mediumFileId = filePrefix + "_medium." + format;
        String smallFileId = filePrefix + "_small." + format;

        try {
            log.info("Uploading avatar for userId={}, format={}", userId, format);

            s3Service.uploadFile(
                    fileId,
                    new ByteArrayInputStream(file.getBytes()),
                    file.getSize(),
                    file.getContentType()
            );

            byte[] mediumImage = resourseService.resize(file.getBytes(), 1080, format);
            s3Service.uploadFile(
                    mediumFileId,
                    new ByteArrayInputStream(mediumImage),
                    mediumImage.length,
                    "image/" + format
            );

            byte[] smallImage = resourseService.resize(file.getBytes(), 170, format);
            s3Service.uploadFile(
                    smallFileId,
                    new ByteArrayInputStream(smallImage),
                    smallImage.length,
                    "image/" + format
            );

            UserProfilePic userProfilePic = new UserProfilePic();
            userProfilePic.setFileId(fileId);
            userProfilePic.setMediumFileId(mediumFileId);
            userProfilePic.setSmallFileId(smallFileId);

            User user = userService.findUserById(userId);
            user.setUserProfilePic(userProfilePic);
            log.info("Avatar uploaded successfully for userId={}", userId);
            return new UploadAvatarResponse(fileId, mediumFileId, smallFileId);

        } catch (IOException e) {
            log.error("Failed to upload avatar for userId={}", userId, e);
            throw new RuntimeException("Failed to upload avatar", e);
        }
    }

    public GetAvatarResponse getAvatar(Long userId) {
        User user = userService.findUserById(userId);
        UserProfilePic profilePic = user.getUserProfilePic();

        if (profilePic == null || profilePic.getFileId() == null) {
            throw new IllegalArgumentException("User does not have an avatar");
        }

        log.info("Generating presigned URLs for userId={}", userId);
        return new GetAvatarResponse(
                s3Service.generatePresignedUrl(profilePic.getFileId()),
                s3Service.generatePresignedUrl(profilePic.getMediumFileId()),
                s3Service.generatePresignedUrl(profilePic.getSmallFileId())
        );
    }

    @Transactional
    public DeleteAvatarResponse deleteAvatar(Long userId) {
        User user = userService.findUserById(userId);
        UserProfilePic profilePic = user.getUserProfilePic();

        if (profilePic == null || profilePic.getFileId() == null) {
            return new DeleteAvatarResponse(false, "User has no avatar");
        }

        log.info("Deleting avatar for userId={}", userId);
        s3Service.deleteFile(profilePic.getFileId());
        s3Service.deleteFile(profilePic.getMediumFileId());
        s3Service.deleteFile(profilePic.getSmallFileId());

        user.setUserProfilePic(null);
        return new DeleteAvatarResponse(true, "Avatar deleted successfully");
    }
}