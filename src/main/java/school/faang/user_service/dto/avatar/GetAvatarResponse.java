package school.faang.user_service.dto.avatar;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAvatarResponse {
    private String avatarUrl;
    private String mediumAvatarUrl;
    private String smallAvatarUrl;
}