package school.faang.user_service.service.user;

import school.faang.user_service.dto.user.GetUserRequest;
import school.faang.user_service.dto.TariffDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

public interface UserService {
    User findById(Long userId);

    TariffDto buyUserTariff(TariffDto tariffDto, Long userId);

    List<UserDto> findUsersByFilter(GetUserRequest request);

    UserDto getUser(long userId);
}
