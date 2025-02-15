package school.faang.user_service.dto.avatar;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadAvatarRequest {
    @NotNull(message = "Avatar file cannot be null")
    @Size(max = 5 * 1024 * 1024, message = "File size must be at most 5MB")
    private MultipartFile file;
}