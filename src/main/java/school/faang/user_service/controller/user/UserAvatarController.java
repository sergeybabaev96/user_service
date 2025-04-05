package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.avatar.UserAvatarProperties;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.service.user.UserAvatarService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/v1/user/avatar")
@RequiredArgsConstructor
@Validated
public class UserAvatarController {

    private final UserAvatarService avatarService;
    private final UserContext userContext;
    private final UserAvatarProperties avatarProperties;

    @PutMapping
    public void uploadAvatar(@RequestBody MultipartFile avatarFile) {
        long maxSizeBytes = avatarProperties.getSizeMB() * 1024 * 1024;
        if (avatarFile.getSize() > maxSizeBytes) {
            throw new MaxUploadSizeExceededException(maxSizeBytes);
        }
        long userId = userContext.getUserId();
        avatarService.uploadAvatar(userId, avatarFile);
    }

    @GetMapping
    public ResponseEntity<InputStreamResource> getAvatarByUser() throws IOException {
        long userId = userContext.getUserId();
        InputStream avatarStream = avatarService.getAvatarByUserId(userId);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(new InputStreamResource(avatarStream));
    }

    @GetMapping("/{key}")
    public ResponseEntity<InputStreamResource> getAvatarByKey(@PathVariable String key) {
        InputStream avatarStream = avatarService.getAvatarByKey(key);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(new InputStreamResource(avatarStream));
    }

    @DeleteMapping
    public void deleteAvatar() {
        long userId = userContext.getUserId();
        avatarService.deleteAvatar(userId);
    }
}
