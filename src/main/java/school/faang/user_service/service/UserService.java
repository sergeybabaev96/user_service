package school.faang.user_service.service;

import org.springframework.http.ResponseEntity;
import school.faang.user_service.dto.UserDto;

public interface UserService {
    ResponseEntity<UserDto> getUser(long userId);
}
