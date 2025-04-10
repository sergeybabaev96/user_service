package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.user.UserService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@Validated
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final S3Service s3Service;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(@RequestBody @Valid UserRegistrationDto dto) {
        User user = userService.registerUser(dto);
        UserResponseDto response = userMapper.toResponseDto(user);

        if (user.getUserProfilePic() != null) {
            String fileId = user.getUserProfilePic().getFileId();
            String presignedUrl = s3Service.getPresingnedUrl(fileId).toString();
            response.setAvatarUrl(presignedUrl);
        }

        return ResponseEntity.ok(response);
    }
    @GetMapping("/avatar/{fileName}")
    public ResponseEntity<byte[]> getAvatar(@PathVariable String fileName) {
        byte[] data = s3Service.getFile(fileName);
        String contentType = s3Service.getContentType(fileName);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(data);
    @PostMapping("/upload-csv")
    public ResponseEntity<String> uploadCsv(@RequestParam("file") @NotNull MultipartFile file){
        try{
            userService.processCsv(file);
            return ResponseEntity.ok("Users imported successfully");
        }catch (IOException e){
            return ResponseEntity.badRequest().body("Error parcessing file " + e.getMessage());
        }
    }
}
