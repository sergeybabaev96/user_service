package school.faang.user_service.dto.avatar;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UploadAvatarRequest {
    @NotNull(message = "Avatar file cannot be null")
    @Size(max = 5 * 1024 * 1024, message = "File size must be at most 5MB")
    private MultipartFile file;
}