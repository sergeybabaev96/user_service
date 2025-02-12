package school.faang.user_service.controller.avatar;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.avatar.*;
import school.faang.user_service.service.avatar.AvatarService;

@RestController
@RequestMapping("/users/{userId}/avatar")
@RequiredArgsConstructor
public class AvatarController {

    private final AvatarService avatarService;

    @PostMapping
    public ResponseEntity<UploadAvatarResponse> uploadAvatar(
            @PathVariable Long userId,
            @RequestParam("file") @Valid MultipartFile file) {

        UploadAvatarRequest request = new UploadAvatarRequest();
        request.setFile(file);

        UploadAvatarResponse response = avatarService.uploadAvatar(userId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<GetAvatarResponse> getAvatar(@PathVariable Long userId) {
        GetAvatarResponse response = avatarService.getAvatar(userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<DeleteAvatarResponse> deleteAvatar(@PathVariable Long userId) {
        DeleteAvatarResponse response = avatarService.deleteAvatar(userId);
        return ResponseEntity.ok(response);
    }
}