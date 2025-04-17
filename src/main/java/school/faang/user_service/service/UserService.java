package school.faang.user_service.service;

import school.faang.user_service.dto.CreateUserDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

public interface UserService {
    long findUniqueIdByUsername(String username);

    User getReferenceById(long userId);

    User findUserById(long userId);

    void checkUserExists(Long userId);

    UserDto deactivateUser();

    boolean existsById(long userId);

    UserDto getUser(long userId);

    List<UserDto> getUsersByIds(List<Long> ids);

    void banUserById(long userId);

    UserDto createUser(CreateUserDto createUserDto);
}