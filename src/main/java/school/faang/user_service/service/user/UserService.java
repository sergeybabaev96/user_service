package school.faang.user_service.service.user;

import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.dto.user.UserRegisterDto;
import school.faang.user_service.dto.user.UserResponseRegisterDto;

import java.util.List;

public interface UserService {

    UserResponseRegisterDto registerUser(UserRegisterDto dto);

    List<UserDto> getAllUsersByFilters(int pageNumber, int pageSize, UserFilterDto filters);

    List<UserDto> getPremiumUsersByFilters(UserFilterDto filters);
}
