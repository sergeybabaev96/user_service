package school.faang.user_service.service.user;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.avatar.AvatarProperties;
import school.faang.user_service.dto.avatar.AvatarType;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.service.external.DiceBearService;
import school.faang.user_service.service.external.S3Service;

import java.net.URL;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserAvatarService {
    private final DiceBearService diceBearService;
    private final S3Service s3Service;
    private final AvatarProperties avatarProperties;

    public void generateAvatarForNewUser(@NotNull User user, @NotNull AvatarType type){
        String fileId = UUID.randomUUID().toString() + type.getExtension();
        byte[] avatarData = diceBearService.generateAvatar(type);
        String bucketName = avatarProperties.getBucketName();
        s3Service.uploadToBucket(fileId, avatarData, type.getContentType());
        URL url = s3Service.getPresingnedUrl(fileId);
        UserProfilePic profilePic = new UserProfilePic();
        profilePic.setFileId(fileId);
        user.setUserProfilePic(profilePic);
    }

    public URL getUserAvatar(@NotNull User user) {
        String fileId = Optional.ofNullable(user.getUserProfilePic())
                .map(UserProfilePic::getFileId)
                .filter(id -> !id.isBlank())
                .orElseThrow(() -> new IllegalArgumentException("У пользователя нет аватара"));

        return s3Service.getPresingnedUrl(fileId);
    }

}
