package school.faang.user_service.controller.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserRegisterDto;
import school.faang.user_service.dto.user.UserResponseRegisterDto;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${domain.path}/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserResponseRegisterDto registerUser(@RequestBody @Valid UserRegisterDto dto) {
        return userService.registerUser(dto);
    }

    @GetMapping("/{id}/followers")
    public List<UserDto> getFollowersByUserId(@PathVariable @NotNull @Min(0) long userId) {
        return userService.getFollowersByUserId(userId);
    }
}
