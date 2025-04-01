package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.user.UserReadDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.UserService;
import school.faang.user_service.validator.UserValidator;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserValidator userValidation;

    @GetMapping("/{userId}")
    public UserReadDto getUser(@PathVariable long userId) {
        return userService.getUser(userId);
    }

    @PostMapping("/list")
    List<UserReadDto> getUsersByIds(@RequestBody List<Long> ids) {
        return userService.getUsersByIds(ids);
    }

    public List<UserReadDto> getPremiumUsers(UserFilterDto filter) {
        return userService.getPremiumUsers(filter);
    }

    public UserReadDto deactivateUser(long userId) {
        userValidation.validateUserId(userId);
        return userService.deactivate(userId);
    }

    @GetMapping("/{userId}/followers/ids")
    public List<Long> getFollowerIds(@PathVariable Long userId) {
        return userService.getFollowerIds(userId);
    }
}
