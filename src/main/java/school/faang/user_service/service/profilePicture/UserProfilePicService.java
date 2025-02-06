package school.faang.user_service.service.profilePicture;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.user.UserCreateDto;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.BusinessException;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UserProfilePicService {
    private final AvatarService avatarService;
    private final RandomAvatarService randomAvatarService;

    public UserProfilePic createProfilePicture(UserCreateDto userCreateDto) {
        String profilePicUrl;
        String thumbnailUrl = null;

        if (userCreateDto.getProfilePic() != null && !userCreateDto.getProfilePic().isEmpty()) {
            MultipartFile profilePic = userCreateDto.getProfilePic();
            try {
                profilePicUrl = avatarService.uploadAvatar(profilePic);
                thumbnailUrl = avatarService.uploadThumbnailAvatar(profilePic);
            } catch (IOException e) {
                throw new BusinessException("Ошибка при загрузке аватарки");
            }
        } else {
            try {
                String seed = userCreateDto.getUsername();
                profilePicUrl = randomAvatarService.generateAndUploadAvatar(seed);
            } catch (IOException e) {
                throw new BusinessException("Ошибка при генерации и загрузке аватарки");
            }
        }

        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(profilePicUrl);
        userProfilePic.setSmallFileId(thumbnailUrl);
        return userProfilePic;
    }
}
