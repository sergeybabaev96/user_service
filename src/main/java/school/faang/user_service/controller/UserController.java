package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.avatar.DeleteAvatarResponse;
import school.faang.user_service.dto.avatar.GetAvatarResponse;
import school.faang.user_service.dto.avatar.UploadAvatarResponse;
import school.faang.user_service.dto.notification.UserChatIdUpdateDto;
import school.faang.user_service.dto.notification.UserNotificationDto;
import school.faang.user_service.service.UserService;
import school.faang.user_service.service.avatar.AvatarService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final AvatarService avatarService;

    @GetMapping("/premium")
    public List<UserDto> getPremiumUsers(@RequestBody UserFilterDto userFilterDto) {
        return userService.getPremiumUsers(userFilterDto);
    }

    @PutMapping("/{id}/deactivate")
    public void deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        return userService.getUserById(userId);
    }

    @GetMapping("/{id}/notification")
    public UserNotificationDto getUserNotificationDto(@PathVariable long id) {
        return userService.getUserNotificationDtoById(id);
    }

    @PutMapping("/chat")
    public UserNotificationDto updateUserChat(@RequestBody UserChatIdUpdateDto userChatIdUpdateDto) {
        return userService.updateUserChatId(userChatIdUpdateDto);
    }

    @PostMapping("/{userId}/avatar")
    public ResponseEntity<UploadAvatarResponse> uploadAvatar(
            @PathVariable Long userId,
            @RequestParam("file") @Valid MultipartFile file) {
        UploadAvatarResponse response = avatarService.uploadAvatar(userId, file);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/avatar")
    public ResponseEntity<GetAvatarResponse> getAvatar(@PathVariable Long userId) {
        GetAvatarResponse response = avatarService.getAvatar(userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}/avatar")
    public ResponseEntity<DeleteAvatarResponse> deleteAvatar(@PathVariable Long userId) {
        DeleteAvatarResponse response = avatarService.deleteAvatar(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/followersIds")
    public List<Long> getFollowers(@PathVariable Long userId) {
        return userService.findFollowersIdById(userId);
    }
}