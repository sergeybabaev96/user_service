package school.faang.user_service.controller.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.user.UserService;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/users")
@Slf4j
public class UserController {

    private UserService userService;
    private UserMapper userMapper;
    private static final String NEGATIVE_ID = "userId is negative";

    @PostMapping("/deactivate")
    @ResponseBody
    public void deactivateUser(@RequestParam Long userId) {
        if (idIsValid(userId)) {
            userService.deactivateUser(userId);
        } else {
            log.error(NEGATIVE_ID);
            throw new DataValidationException(NEGATIVE_ID);
        }
    }

    @GetMapping("{userId}")
    @ResponseBody
    public UserDto getUser(@PathVariable Long userId) {
        if(idIsValid(userId)) {
            User user = userService.getUser(userId);
            return userMapper.toDto(user);
        } else {
            log.error(NEGATIVE_ID);
            throw new DataValidationException(NEGATIVE_ID);
        }
    }

    private boolean idIsValid(Long id) {
        return id >= 0;
    }
}
