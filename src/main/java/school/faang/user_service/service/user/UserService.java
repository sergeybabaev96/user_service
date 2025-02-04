package school.faang.user_service.service.user;

import school.faang.user_service.dto.user.UserRegisterDto;
import school.faang.user_service.dto.user.UserResponseRegisterDto;

public interface UserService {

    UserResponseRegisterDto registerUser(UserRegisterDto dto);
}
