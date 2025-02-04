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
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.EventService;
import school.faang.user_service.service.GoalService;
import school.faang.user_service.service.MentorshipService;
import school.faang.user_service.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("${user-service.api-version}/users")
public class UserController {
    private final UserService userService;
    private final MentorshipService mentorshipService;
    private final GoalService goalService;
    private final EventService eventService;
    private final UserMapper userMapper;


    @PostMapping("/deactivate")
    @Transactional
    public UserDto deactivateUser(@RequestParam("user_id") long userId) {
        // before deactivation
        goalService.deactivateGoalsByUserId(userId);
        eventService.deactivateEventsByUserId(userId);
        // deactivation
        User user = userService.deactivateUser(userId);
        // after deactivation
        mentorshipService.deactivateMentorship(userId);
        return userMapper.toUserDto(user);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable long userId) {
        return userService.getUser(userId);
    }
}
