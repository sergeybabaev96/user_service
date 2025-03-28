package school.faang.user_service.validation.user;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.exception.DataValidationException;

@Component
public class UserValidator {
    private final long MAX_FILE_SIZE =  5242880;

    public void checkAvatarSize(MultipartFile avatar){
        long avatarSize = avatar.getSize();
        if (avatarSize > MAX_FILE_SIZE) {
            throw new DataValidationException("Avatar size cant be more 5 mb");
        }
    }

}
