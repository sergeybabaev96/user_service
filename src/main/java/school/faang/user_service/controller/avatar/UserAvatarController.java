package school.faang.user_service.controller.avatar;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.avatar.AvatarResponseDto;
import school.faang.user_service.service.UserAvatarService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/avatar")
public class UserAvatarController {
    private final UserAvatarService avatarService;

    @PostMapping("/{userId}")
    public ResponseEntity<AvatarResponseDto> uploadAvatar(@PathVariable @Positive long userId,
                                                          @RequestBody MultipartFile file) {
        AvatarResponseDto dto = avatarService.uploadAvatar(userId, file);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<byte[]> getAvatar(@PathVariable long userId,
                                            @RequestParam("size") String size) {
        byte[] imageBytes = avatarService.getAvatar(userId, size);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(imageBytes);
    }


    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteAvatar(@PathVariable Long userId) {
        avatarService.deleteAvatar(userId);
        return ResponseEntity.ok().build();
    }
}

