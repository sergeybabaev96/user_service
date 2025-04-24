package school.faang.user_service.controller.avatar;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
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
import school.faang.user_service.service.avatar.AvatarService;

import java.io.InputStream;

@RestController
@RequestMapping("/users/avatar")
@RequiredArgsConstructor
public class AvatarController {

    private final AvatarService avatarService;

    @PostMapping("/{userId}/new")
    public ResponseEntity<HttpStatus> addUserAvatar(
            @PathVariable @Min(1) @NotNull Long userId,
            @RequestBody @NotNull MultipartFile file) {
        avatarService.addUserAvatar(userId, file);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<InputStreamResource> getUserAvatar(
            @PathVariable @Min(1) @NotNull Long userId,
            @RequestParam(value = "isSmall", required = false, defaultValue = "false") Boolean isSmall) {
        InputStream avatar = avatarService.getUserAvatar(userId, isSmall);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(new InputStreamResource(avatar));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> removeUserAvatar(
            @PathVariable @Min(1) @NotNull Long userId) {
        avatarService.removeUserAvatar(userId);
        return ResponseEntity.noContent().build();
    }

}
