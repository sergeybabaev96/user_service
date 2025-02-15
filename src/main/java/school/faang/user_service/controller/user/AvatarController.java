package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.service.user.AvatarService;

import java.io.InputStream;

@RestController
@RequiredArgsConstructor
@RequestMapping("${domain.path}/avatars")
@Validated
public class AvatarController {

    private final AvatarService avatarService;

    @PostMapping("/users/{userId}")
    public void saveAvatar(@PathVariable long userId, @RequestBody MultipartFile file) {
        avatarService.saveAvatars(userId, file);
    }

    @GetMapping("/{key}")
    public InputStream getAvatar(@PathVariable String key) {
        return avatarService.getAvatar(key);
    }

    @DeleteMapping("/{key}")
    public void deleteAvatar(@PathVariable String key) {
        avatarService.deleteAvatar(key);
    }
}
