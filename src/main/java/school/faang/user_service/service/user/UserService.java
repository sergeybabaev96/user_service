package school.faang.user_service.service.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.TariffDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.user.GetUserRequest;

import java.util.List;

public interface UserService {
    ResponseEntity<UserDto> getUser(long userId);

    TariffDto buyUserTariff(TariffDto tariffDto, Long userId);

    List<UserDto> findUsersByFilter(GetUserRequest request);

    UserDto deactivateUser(long userId);

    void saveProfilePic(long userId, MultipartFile pic);
}
