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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.service.avatar.AvatarService;

import java.io.InputStream;

@RestController
@RequestMapping("/api/v1/users/{userId}/avatar")
@RequiredArgsConstructor
public class AvatarController {

    private final AvatarService avatarService;

    @PostMapping("/new")
    public ResponseEntity<HttpStatus> addUserAvatar(
            @PathVariable @Min(1) @NotNull Long userId,
            @RequestBody @NotNull MultipartFile file) {
        avatarService.addUserAvatar(userId, file);
        System.out.println(file.getOriginalFilename());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/get")
    public ResponseEntity<InputStreamResource> getUserAvatar(
            @PathVariable @NotNull Long userId) {
        InputStream avatar = avatarService.getUserAvatar(userId);
        if (avatar == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(new InputStreamResource(avatar));
    }

    @DeleteMapping()
    public ResponseEntity<Void> removeUserAvatar(
            @PathVariable @NotNull Long userId) {
        avatarService.removeUserAvatar(userId);
        return ResponseEntity.noContent().build();
    }

}
