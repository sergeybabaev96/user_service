package school.faang.user_service.validator.avatar;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.exception.FileSizeExceedLimitException;

@Component
@Slf4j
public class FileSizeValidator {

    public void checkMaxFileSize(MultipartFile file, Long maxFileSize) {
        if (file.getSize() > maxFileSize) {
            log.error("Error: File size - {} must be less than {} bytes", file.getOriginalFilename(), maxFileSize);
            throw new FileSizeExceedLimitException("File size exceeds the permissible size");
        }
    }

}
