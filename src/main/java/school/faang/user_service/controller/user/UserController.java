package school.faang.user_service.controller.user;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.user.UserService;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    public List<UserDto> getPremiumUsers(UserFilterDto userFilterDto) {
        if (userFilterDto == null) {
            return userService.getPremiumUsers();
        }
        return userService.getPremiumUsers(userFilterDto);
    }

    @GetMapping("/users/{id}")
    public UserDto getUser(@PathVariable Long id) {
        return userMapper.toDto(userService.getUser(id));
    }

}
