package school.faang.user_service.service.user;

import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserRegisterDto;
import school.faang.user_service.dto.user.UserResponseRegisterDto;

import java.util.List;

public interface UserService {

    UserResponseRegisterDto registerUser(UserRegisterDto dto);

    List<UserDto> getFollowersByUserId(long userId);
}
