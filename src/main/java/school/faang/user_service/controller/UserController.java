package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.file.FileUploadResponseDto;
import school.faang.user_service.service.UserService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/")
    public FileUploadResponseDto uploadCSV(@RequestBody MultipartFile file) {
        return userService.processPersonsFromFile(file);
    }
}
