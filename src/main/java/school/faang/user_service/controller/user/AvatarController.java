package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.service.user.AvatarService;

import java.io.InputStream;

@RestController
@RequiredArgsConstructor
@RequestMapping("${domain.path}/avatars")
@Validated
public class AvatarController {

    private final AvatarService avatarService;

    @PostMapping("/user/{userId}")
    public void saveAvatar(@PathVariable long userId, @RequestBody MultipartFile file) {
        avatarService.saveAvatars(userId, file);
    }

    @GetMapping(value = "/user/{userId}")
    public ResponseEntity<InputStreamResource> getAvatarByUser(@PathVariable long userId, @RequestParam String size) {
        InputStream avatar = avatarService.getAvatarByUser(userId, size);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(new InputStreamResource(avatar));
    }

    @GetMapping("/{key}")
    public ResponseEntity<InputStreamResource> getAvatarByKey(@PathVariable String key) {
        InputStream avatar = avatarService.getAvatarByKey(key);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(new InputStreamResource(avatar));
    }

    @DeleteMapping("/user/{userId}")
    public void deleteAvatarByUser(@PathVariable long userId) {
        avatarService.deleteAvatarByUser(userId);
    }

    @DeleteMapping("/{key}")
    public void deleteAvatarByKey(@PathVariable String key) {
        avatarService.deleteAvatarByKey(key);
    }
}
