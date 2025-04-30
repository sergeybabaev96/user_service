package school.faang.user_service.validator.avatar;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.exception.FileSizeExceedLimitException;

@Component
@Slf4j
public class AvatarValidator {

    public void checkMaxFileSize(MultipartFile file, Long maxFileSize) {
        if (file.getSize() > maxFileSize) {
            log.error("Error: File size - {} must be less than {} bytes", file.getOriginalFilename(), maxFileSize);
            throw new FileSizeExceedLimitException("File size exceeds the permissible size");
        }
    }

    public void checkAvatarKey(String avatarKey) {
        if(avatarKey == null || avatarKey.isBlank()) {
            log.error("Avatar with key {} - not found!", avatarKey);
            throw new EntityNotFoundException("User's avatar not found!");
        }
    }

}
