package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.UserService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PutMapping("/deactivate/{id}")
    public UserDto deactivateUser(@PathVariable Long id) {
        return userService.deactivateUser(id);
    }

    @PutMapping("/activate/{id}")
    public UserDto activateUser(@PathVariable Long id) {
        return userService.activateUser(id);
    }
}
