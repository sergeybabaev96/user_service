package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.UserService;

@Service
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    public UserDto deactivateUser(Long userId) {
        return userService.deactivateUser(userId);
    }

}
