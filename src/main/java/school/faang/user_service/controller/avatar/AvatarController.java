package school.faang.user_service.controller.avatar;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/avatar")
@Tag(name = "User Avatars", description = "API for managing user profile avatars")
public class AvatarController {
    private final AvatarService avatarService;

    @Operation(
            summary = "Upload user avatar",
            description = "Upload or update a user's profile picture"
    )
    @PostMapping("/add")
    public ResponseEntity<Void> addUserAvatar(
            @Parameter(description = "ID of the user", example = "123", required = true)
            @PathVariable
            @NotNull Long userId,
            @Parameter(description = "Image file (JPEG/PNG, max 5MB)", required = true)
            @RequestBody
            @NotNull MultipartFile file) {

        avatarService.addUserAvatar(userId, file);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/get")
    @Operation(
            summary = "Get user avatar",
            description = "Retrieve the user's profile picture"
    )
    public ResponseEntity<Resource> getUserAvatar(
            @Parameter(description = "ID of the user", example = "123", required = true)
            @PathVariable
            @NotNull Long userId) {
        Resource resource = avatarService.getUserAvatar(userId);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }

    @Operation(
            summary = "Delete user avatar",
            description = "Remove the user's profile picture"
    )
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteUserAvatar(
            @Parameter(description = "ID of the user", example = "123", required = true)
            @PathVariable
            @NotNull Long userId) {
        avatarService.deleteUserAvatar(userId);
        return ResponseEntity.noContent().build();
    }
}
