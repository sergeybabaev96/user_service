package school.faang.user_service.dto.user;

import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.Country;

@Builder
@Data
public class UserProfile {
    private Long userId;
    private String username;
    private String aboutMe;
    private String country;
    private Integer experience;
    private String avatarUrl;
}
