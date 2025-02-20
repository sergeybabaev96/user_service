package school.faang.user_service.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.DeactivateUserFacade;
import school.faang.user_service.service.EventService;
import school.faang.user_service.service.GoalService;
import school.faang.user_service.service.UserService;
import school.faang.user_service.service.mentorship.MentorshipService;

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
}
