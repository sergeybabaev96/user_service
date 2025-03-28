package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserViewDto;
import school.faang.user_service.service.UserService;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PutMapping("/{userId}/avatar/add")
    public ResponseEntity<UserViewDto> addUserAvatar(@PathVariable Long userId, @RequestBody MultipartFile file) {
            UserViewDto userViewDto = userService.addUserAvatar(userId, file);
            return ResponseEntity.ok(userViewDto);
    }

    @GetMapping("/{userId}/avatar/get")
    public ResponseEntity<byte[]> getUserAvatar(@PathVariable Long userId) {
        InputStream inputStream = userService.getUserAvatar(userId);
        byte[] image;
        try {
            image = inputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);

        return new ResponseEntity<>(image,headers, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}/avatar/delete")
    public ResponseEntity<Void> deleteUserAvatar(@PathVariable Long userId) {
        userService.deleteUserAvatar(userId);
        return ResponseEntity.noContent().build();
    }
}
