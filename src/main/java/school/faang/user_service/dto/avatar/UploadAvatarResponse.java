package school.faang.user_service.dto.avatar;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadAvatarResponse {
    private String fileId;
    private String mediumFileId;
    private String smallFileId;
}