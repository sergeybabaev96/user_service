package school.faang.user_service.entity.user_cache;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import school.faang.user_service.dto.user.UserCacheProfilePicDto;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserCacheDto {

    private long userId;
    private String username;
    private boolean active;
    private UserCacheProfilePicDto profilePicture;
}
