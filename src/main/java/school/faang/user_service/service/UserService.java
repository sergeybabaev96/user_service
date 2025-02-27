package school.faang.user_service.service;

import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.user.UserResponseDto;
import school.faang.user_service.entity.User;

public interface UserService {
    void processPersonsFromFile(MultipartFile file);

    User findByIdOrThrow(long userId);

    UserResponseDto getUser(Long userId);
}
