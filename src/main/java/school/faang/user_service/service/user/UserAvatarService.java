package school.faang.user_service.service.user;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import school.faang.user_service.dto.avatar.AvatarType;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.service.external.DiceBearService;
import school.faang.user_service.service.external.S3Service;

import java.net.URL;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Validated
@Service
public class UserAvatarService {
    private final S3Service s3Service;
    private final DiceBearService diceBearService;

    @Value("${avatar.bucketName}")
    private String bucketName;

    public void generateAvatarForNewUser(@NotNull User user, @NotNull AvatarType type) {
        String randomName = UUID.randomUUID().toString();
        byte[] avatarData = diceBearService.generateAvatar(randomName, type);
        String avatarFileName = randomName + type.getExtension();

        s3Service.uploadToBucket(avatarFileName, avatarData, type.getContentType());

        URL avatarUrl = s3Service.getUnexpiredUrl(bucketName, avatarFileName);

        UserProfilePic profilePic = new UserProfilePic();
        profilePic.setFileId(avatarFileName);
        user.setUserProfilePic(profilePic);

        user.setAboutMe(avatarUrl.toString());
    }

    public URL getUserAvatar(@NotNull User user) {
        String fileId = Optional.ofNullable(user.getUserProfilePic())
                .map(UserProfilePic::getFileId)
                .filter(id -> !id.isBlank())
                .orElseThrow(() -> new NoSuchElementException("No avatar for user " + user.getId()));

        return s3Service.getUnexpiredUrl(bucketName, fileId);
    }
}