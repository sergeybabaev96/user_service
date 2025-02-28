package school.faang.user_service.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.BuyTariffRequest;
import school.faang.user_service.dto.TariffDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.user.GetUserRequest;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.service.DeactivateUserFacade;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${user-service.api-version}/users")
public class UserController {

    private final UserService userService;
    private final DeactivateUserFacade deactivateUserFacade;


    @PostMapping("/deactivate")
    public UserDto deactivateUser(@RequestParam("user_id") long userId) {
        return deactivateUserFacade.deactivateUser(userId);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable long userId) {
        return userService.getUser(userId);
    }

    @PostMapping("/buy-tariff")
    public TariffDto buyTariff(@RequestBody BuyTariffRequest request) {
        return userService.buyUserTariff(request.tariffDto(), request.id());
    }

    @GetMapping("/users")
    public List<UserDto> getUsers(@RequestBody GetUserRequest request) {
        return userService.findUsersByFilter(request);
    }

    @PostMapping("/upload-profile-pic")
    public void uploadProfilePic(@RequestParam("user_id") long userId, @RequestParam("pic") MultipartFile pic) {
        userService.saveProfilePic(userId, pic);
    }
}
