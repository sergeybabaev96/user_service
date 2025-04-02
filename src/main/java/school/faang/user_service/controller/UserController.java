package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor

@RequestMapping("api/v1/users")
@Tag(name = "User API", description = "Super API to interact with users table")
public class UserController {
    private final UserService userService;

    // TODO: задача BJS2-66001 сделана неверно
//    @PutMapping("/deactivate/{userId}")
//    public UserDto deactivateUser(@PathVariable Long userId) {
//        return userService.deactivateUser(userId);
//    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user by id", description = "Returns a user DTO")
    public UserDto getUser(@PathVariable long userId) {
        return userService.getUser(userId);
    }

    @PostMapping()
    @Operation(summary = "Get users by ids", description = "Returns a list of user DTOs")
    public List<UserDto> getUsersByIds(@RequestBody List<Long> ids) {
        return userService.getUsersByIds(ids);
    }
}
