package school.faang.user_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.BooleanResponse;
import school.faang.user_service.dto.user.UserCreateDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.publisher.ProfileViewEventPublisher;
import school.faang.user_service.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ProfileViewEventPublisher publisherService;

    @GetMapping("/profile/{userId}")
    public void viewProfile(@PathVariable long userId) {
        //код просмотра профиля
        publisherService.publisherEvent(userId);
    }

    @DeleteMapping("/{userId}")
    public void deactivateUser(@RequestParam @NotNull Long userId) {
        userService.deactivateUser(userId);
    }

    @GetMapping("/is-user-exist/{userId}")
    public BooleanResponse isUserExist(@RequestParam(name = "user_id") Long userId) {
        return new BooleanResponse(userService.isUserExist(userId));
    }

    @GetMapping("/premium")
    public List<UserDto> getPremiumUsers(@RequestBody(required = false) UserFilterDto userFilterDto) {
        return userService.getPremiumUsers(userFilterDto);
    }

    @PostMapping(value = "/registration", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserDto createUser(@Valid @ModelAttribute UserCreateDto userCreateDto) {
        return userService.createUser(userCreateDto);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        return userService.getUser(userId);
    }

    @GetMapping
    public List<UserDto> getUsersByIds(@RequestParam List<Long> ids) {
        return userService.getUsersByIds(ids);
    }
}
