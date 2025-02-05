package school.faang.user_service.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
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

    @GetMapping("/premium")
    public List<UserDto> getPremiumUsersByFilters(UserFilterDto filters) {
        return userService.getPremiumUsersByFilters(filters);
    }

    @GetMapping("/page/{pageNumber}/size/{pageSize}")
    public List<UserDto> getAllUsersByFilter(@PathVariable int pageNumber, @PathVariable int pageSize,
                                             UserFilterDto filters) {
        return userService.getAllUsersByFilters(pageNumber, pageSize, filters);
    }
}
