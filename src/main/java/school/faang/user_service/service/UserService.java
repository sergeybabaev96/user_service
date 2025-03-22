package school.faang.user_service.service;

import school.faang.user_service.dto.CreateUserDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

public interface UserService {
    boolean doesUserExist(long userId);

    User getUserById(long userId);

    User findById(long userId);

    boolean existsById(long userId);

    UserDto createUser(CreateUserDto createUserDto);
}
