package school.faang.user_service.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.filter.user.UserFilterDto;
import school.faang.user_service.dto.user.UserRegisterDto;
import school.faang.user_service.dto.user.UserResponseRegisterDto;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@Tag(name = "Пользователи")
@RestController
@RequiredArgsConstructor
@RequestMapping("${domain.path}/users")
@Validated
public class UserController {
    private final UserService userService;

    @Operation(summary = "Регистрация пользователя")
    @PostMapping
    public UserResponseRegisterDto registerUser(@RequestBody @Valid UserRegisterDto dto) {
        return userService.registerUser(dto);
    }

    @Operation(summary = "Получить премиум пользователей по фильтрам")
    @GetMapping("/page/{pageNumber}/size/{pageSize}/premium")
    public List<UserDto> getPremiumUsersByFilters(@PathVariable @Min(0) int pageNumber,
                                                  @PathVariable @Min(1) int pageSize,
                                                  UserFilterDto filters) {
        return userService.getPremiumUsersByFilters(pageNumber, pageSize, filters);
    }

    @Operation(summary = "Получить всех пользователей по фильтрам")
    @GetMapping("/page/{pageNumber}/size/{pageSize}")
    public List<UserDto> getAllUsersByFilter(@PathVariable @Min(0) int pageNumber,
                                             @PathVariable @Min(1) int pageSize,
                                             UserFilterDto filters) {
        return userService.getAllUsersByFilters(pageNumber, pageSize, filters);
    }
}
