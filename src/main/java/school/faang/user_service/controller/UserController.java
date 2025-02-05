package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.file.FileUploadResponseDto;
import school.faang.user_service.service.UserService;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/")
    public FileUploadResponseDto addResource(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                             @RequestBody MultipartFile file) {
        log.info("Start uploading file {}", file.getOriginalFilename());
        InputStream fileInputStream;
        try {
            fileInputStream = file.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        FileUploadResponseDto resultDto = userService.parseCsv(fileInputStream);
        log.info("Finish uploading file {}", file.getOriginalFilename());
        return resultDto;

    }
}
