package school.faang.user_service.service.user;

import school.faang.user_service.dto.user.UserDto;

import java.util.List;

public interface FollowerService {
    List<UserDto> getFollowersByUserId(long userId);
}
