package school.faang.user_service.controller.user;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validation.image.ValidImage;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@RestController
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/{userId}")
    ResponseEntity<UserDto> getUser(@PathVariable @Positive long userId) {
        User user = userService.getUser(userId);
        UserDto userDto = userMapper.toDto(user);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping
    ResponseEntity<List<UserDto>> getUsersByIds(@RequestBody List<UserDto> users) {
        List<User> userList = userMapper.toUserList(users);
        userList = userService.getUsersByIds(userList);
        List<UserDto> userDtoList = userMapper.toUserDtoList(userList);
        return ResponseEntity.ok(userDtoList);
    }

    @DeleteMapping("/deactivate")
    public void deactivateUser(@RequestParam("userId") Long userId) {
        userService.deactivateUser(userId);
    }

    @PostMapping("/avatar")
    public ResponseEntity<String> uploadAvatar(
            @ValidImage @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "small") String size) {
        String avatarUrl = userService.uploadAvatar(file, size);
        return ResponseEntity.ok(avatarUrl);
    }

    @GetMapping("/avatar")
    public ResponseEntity<String> downloadAvatar(@RequestParam(defaultValue = "small") String size) {
        String avatarUrl = userService.downloadAvatar(size);
        return ResponseEntity.ok(avatarUrl);
    }

    @DeleteMapping("/avatar")
    public ResponseEntity<Void> deleteAvatar() {
        userService.deleteAvatar();
        return ResponseEntity.noContent().build();
    }
}
