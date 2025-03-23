package school.faang.user_service.service.user;

import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

public interface UserService {
    User findUserById(Long userId);

    void saveUser(User user);

    UserDto getUser(Long userId);

    List<UserDto> getUsersByIds(List<Long> ids);
}
