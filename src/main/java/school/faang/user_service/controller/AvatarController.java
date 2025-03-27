package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.AvatarService;

@RestController
@RequestMapping("/avatar")
@RequiredArgsConstructor
@Tag(name = "Avatar API", description = "API для управления аватарами пользователя")
public class AvatarController {
    private final AvatarService avatarService;

    @PostMapping("/generate")
    @Operation(summary = "Генерация аватара", description = "Генерирует аватар пользователя на основе переданных данных")
    public String generateAvatar(@RequestBody UserDto userDto) {
        return avatarService.generateAndUploadAvatar(userDto);
    }
}
