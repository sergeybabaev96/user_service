package school.faang.user_service.controller.avatar;

import jakarta.validation.constraints.Positive;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.service.external.UserAvatarService;

@RestController
@RequestMapping("/avatar")
@RequiredArgsConstructor
@Validated
public class UserAvatarController {
    private final UserAvatarService userAvatarService;

    @PostMapping("/{userId}")
    public ResponseEntity<UserProfilePic> uploadAvatar(@PathVariable @NonNull @Positive Long userId,
                                                       @RequestParam("file") MultipartFile file) {
        UserProfilePic userProfilePic = userAvatarService.uploadAvatar(userId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(userProfilePic);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable @NonNull @Positive Long userId) {
        byte[] image = userAvatarService.getProfilePicture(userId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                .body(image);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteProfilePic(@PathVariable @NonNull @Positive Long userId) {
        userAvatarService.deleteProfilePic(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
