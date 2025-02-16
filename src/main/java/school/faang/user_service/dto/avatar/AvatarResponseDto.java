package school.faang.user_service.dto.avatar;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AvatarResponseDto {
    private Long userId;
    private String smallImageKey;
    private String largeImageKey;
}