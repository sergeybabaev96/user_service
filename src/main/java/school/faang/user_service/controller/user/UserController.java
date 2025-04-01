package school.faang.user_service.controller.user;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.service.user.UserService;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@Validated
public class UserController {

    private final UserService userService;

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
