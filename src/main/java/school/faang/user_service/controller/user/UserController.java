package school.faang.user_service.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserRegisterDto;
import school.faang.user_service.dto.user.UserResponseRegisterDto;
import school.faang.user_service.service.user.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("${domain.path}/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserResponseRegisterDto registerUser(@RequestBody @Valid UserRegisterDto dto) {
        return userService.registerUser(dto);
    }
}
