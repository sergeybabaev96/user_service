package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.user.UserResponseDto;
import school.faang.user_service.service.users.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.service.UserService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UsersService usersService;

    @GetMapping("/{userId}")
    public UserResponseDto getUser(@PathVariable Long userId) {
        return usersService.getUser(userId);
    private final UserService userService;

    @PostMapping("/import")
    @ResponseStatus(HttpStatus.CREATED)
    public void importUsers(@RequestBody MultipartFile file) {
        userService.processPersonsFromFile(file);
    }
}
